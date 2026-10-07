package com.console.payment.channel.strategy.u2c;

import com.console.framework.utils.TenantUtils;
import com.console.payment.channel.PaymentStrategy;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.u2c.dto.req.CashOutOrderReq;
import com.console.payment.channel.strategy.u2c.dto.req.PayOrderReq;
import com.console.payment.channel.strategy.u2c.dto.req.QueryBalanceReq;
import com.console.payment.channel.strategy.u2c.dto.req.QueryOrderReq;
import com.console.payment.channel.strategy.u2c.dto.res.BalanceRes;
import com.console.payment.channel.strategy.u2c.dto.res.CallBackRes;
import com.console.payment.channel.strategy.u2c.dto.res.CashOutRes;
import com.console.payment.channel.strategy.u2c.dto.res.PayRes;
import com.console.payment.channel.strategy.u2c.dto.res.U2CResponse;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentChannel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.utils.PaymentUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * U2C 渠道适配器，负责把系统统一订单模型转换为 U2C 协议模型。
 */
@Slf4j
@Service("u2cPay")
public class U2CStrategy extends PaymentStrategy {
    private final U2CPaymentApi paymentApi;
    private final ObjectMapper objectMapper;

    public U2CStrategy(U2CPaymentApi paymentApi, ObjectMapper objectMapper) {
        this.paymentApi = paymentApi;
        this.objectMapper = objectMapper;
    }

    /** 组装 U2C 充值请求并发起下单。 */
    @Override
    public ChannelResult createPayment(PaymentOrder order, PaymentChannel channel, String serviceCode) {
        PayOrderReq request = new PayOrderReq();
        request.setMerchantId(channel.getAppKey());
        request.setMerchantOrderNo(serviceCode + "_" + TenantUtils.getTenantId() + "_" + order.getSiteId() + "_" + order.getId());
        request.setAmount(PaymentUtils.toCents(order.getOrderAmt()));
        request.setPayType(channel.getPayType());
        request.setCurrency(channel.getCurType());
        request.setContent("recharge");
        request.setClientIp(order.getIp());
        request.setCallback(channel.getPayCallBackUrl());
        request.setKycPayerIdNo(order.getPayerIdNo());
        request.setKycPayerName(order.getPayerName());
        request.setSign(PaymentUtils.sign(request, channel.getApiSecret()));
        U2CResponse<PayRes> response = execute(paymentApi.createPayment(channel.getPayUrl(), request));
        return response.isSuccess() ? toChannelResult(response.getData()) : toFailureResult(response);
    }

    /** 使用本地商户订单号查询 U2C 充值状态。 */
    @Override
    public ChannelResult queryPayment(PaymentOrder order, PaymentChannel channel) {
        U2CResponse<PayRes> response = execute(paymentApi.queryPayment(channel.getPaySearchUrl(),
                queryRequest(channel, order.getMerchantOrderNo())));
        return response.isSuccess() ? toChannelResult(response.getData()) : toFailureResult(response);
    }

    /** 组装 PIX 收款信息并发起提现。 */
    @Override
    public ChannelResult createCashOut(CashOutOrder order, PaymentChannel channel, BankCard bankCard) {
        U2CResponse<CashOutRes> response = execute(paymentApi.createCashOut(channel.getCashOutUrl(),
                new CashOutOrderReq(order, channel, bankCard, "CASH")));
        return response.isSuccess() ? toChannelResult(response.getData()) : toFailureResult(response);
    }

    /** 查询 U2C 提现状态。 */
    @Override
    public ChannelResult queryCashOut(CashOutOrder order, PaymentChannel channel) {
        U2CResponse<CashOutRes> response = execute(paymentApi.queryCashOut(channel.getCashOutSearchUrl(),
                queryRequest(channel, order.getMerchantOrderNo())));
        return response.isSuccess() ? toChannelResult(response.getData()) : toFailureResult(response);
    }

    @Override
    public List<BalanceRes> queryBalance(PaymentChannel channel) {
        QueryBalanceReq request = new QueryBalanceReq();
        request.setMerchantId(channel.getAppKey());
        request.setSign(PaymentUtils.sign(request, channel.getApiSecret()));
        U2CResponse<List<BalanceRes>> response = execute(paymentApi.queryBalance(channel.getBalanceQueryUrl(), request));
        if (!response.isSuccess()) {
            // 当前余额接口只返回 List，不能把调用失败伪装成“余额为零”。
            throw new IllegalStateException(response.getMessage());
        }
        return response.getData();
    }

    @Override
    public boolean verifyCallback(Map<String, Object> payload, PaymentChannel channel) {
        // sign 本身不参与签名；PaymentUtils 会同时过滤 null 和空字符串字段。
        Object provided = payload.get("sign");
        return provided != null && PaymentUtils.sign(payload, channel.getApiSecret())
                .equalsIgnoreCase(String.valueOf(provided));
    }

    @Override
    public ChannelResult parsePaymentCallback(Object payload, PaymentChannel channel) {
        return parseVerifiedCallback(payload, channel);
    }

    @Override
    public ChannelResult parseCashOutCallback(Object payload, PaymentChannel channel) {
        return parseVerifiedCallback(payload, channel);
    }

    /**
     * 回调必须先验签再转换业务结果，避免未经验证的数据进入订单状态机。
     */
    @SuppressWarnings("unchecked")
    private ChannelResult parseVerifiedCallback(Object payload, PaymentChannel channel) {
        if (payload == null || channel == null) {
            throw new IllegalArgumentException("callback payload or payment channel is missing");
        }
        Map<String, Object> fields = objectMapper.convertValue(payload, Map.class);
        if (!verifyCallback(fields, channel)) {
            throw new IllegalArgumentException("invalid U2C callback signature");
        }
        return toChannelResult(objectMapper.convertValue(payload, CallBackRes.class));
    }

    private QueryOrderReq queryRequest(PaymentChannel channel, String merchantOrderNo) {
        QueryOrderReq request = new QueryOrderReq(channel.getAppKey(), merchantOrderNo, null);
        request.setSign(PaymentUtils.sign(request, channel.getApiSecret()));
        return request;
    }

    /**
     * 同步执行 Retrofit 请求，将 HTTP、网络及 U2C 业务错误转换成失败响应。
     * 订单调用方据此保留当前状态，避免一次临时故障中断整批状态同步。
     */
    private <T> U2CResponse<T> execute(Call<U2CResponse<T>> call) {
        long started = System.currentTimeMillis();
        String url = call.request().url().toString();
        try {
            Response<U2CResponse<T>> httpResponse = call.execute();
            U2CResponse<T> response = httpResponse.body();
            log.info("U2C request completed url={}, costMs={}, httpCode={}, success={}, errorCode={}", url,
                    System.currentTimeMillis() - started, httpResponse.code(),
                    response != null && response.isSuccess(), response == null ? null : response.getErrorCode());
            if (!httpResponse.isSuccessful()) {
                return failure("HTTP_" + httpResponse.code(), "U2C HTTP error: " + httpResponse.code(),
                        httpResponse.code() >= 500);
            }
            if (response == null) {
                return failure("EMPTY_RESPONSE", "empty U2C response", true);
            }
            return response;
        } catch (IOException ex) {
            log.warn("U2C request failed url={}, costMs={}, message={}", url,
                    System.currentTimeMillis() - started, ex.getMessage());
            return failure("NETWORK_ERROR", "U2C network request failed: " + ex.getMessage(), true);
        }
    }

    private <T> U2CResponse<T> failure(String errorCode, String message, boolean retryable) {
        U2CResponse<T> response = new U2CResponse<>();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setRetryable(retryable);
        return response;
    }

    private ChannelResult toFailureResult(U2CResponse<?> response) {
        ChannelResult result = new ChannelResult();
        result.setSuccess(false);
        result.setErrorCode(response.getErrorCode());
        result.setRetryable(response.isRetryable());
        result.setMessage(response.getMessage());
        return result;
    }

    private ChannelResult toChannelResult(PayRes result) {
        ChannelResult value = baseResult(result.getMerchantId(), result.getMerchantOrderNo(), result.getOrderNo(),
                result.getAmount(), result.getCurrency(), result.getStatus());
        value.setPayUrl(result.getPayUrl());
        value.setPayRaw(result.getPayRaw());
        return value;
    }

    private ChannelResult toChannelResult(CashOutRes result) {
        ChannelResult value = baseResult(result.getMerchantId(), result.getMerchantOrderNo(), result.getOrderNo(),
                result.getAmount(), result.getCurrency(), result.getStatus());
        value.setSuccess(result.getErrorMsg() == null || result.getErrorMsg().isBlank());
        value.setMessage(result.getErrorMsg());
        return value;
    }

    private ChannelResult toChannelResult(CallBackRes result) {
        ChannelResult value = baseResult(result.getMerchantId(), result.getMerchantOrderNo(), result.getOrderNo(),
                result.getPaidAmount() == null ? result.getAmount() : result.getPaidAmount(),
                result.getCurrency(), result.getStatus());
        value.setMessage(result.getErrorMsg());
        return value;
    }

    private ChannelResult baseResult(String merchantId, String merchantOrderNo, String orderNo,
                                     Integer amount, String currency, String status) {
        ChannelResult value = new ChannelResult();
        value.setSuccess(true);
        value.setMerchantId(merchantId);
        value.setMerchantOrderNo(merchantOrderNo);
        value.setOrderNo(orderNo);
        value.setAmount(amount);
        value.setCurrency(currency);
        value.setStatus(status);
        return value;
    }
}
