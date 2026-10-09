package com.console.payment.channel.strategy.u2c;

import com.alibaba.fastjson.JSON;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.payment.channel.strategy.ChannelStrategy;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.u2c.req.CashOutOrderReq;
import com.console.payment.channel.strategy.u2c.req.PayOrderReq;
import com.console.payment.channel.strategy.u2c.req.QueryBalanceReq;
import com.console.payment.channel.strategy.u2c.req.QueryOrderReq;
import com.console.payment.channel.strategy.u2c.res.*;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.utils.PaymentUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

/**
 * U2C 渠道适配器，负责把系统统一订单模型转换为 U2C 协议模型。
 */
@Slf4j
@Service("u2cPay")
public class U2CStrategy extends ChannelStrategy {
    @Resource
    private U2CPaymentApi paymentApi;

    /** 组装 U2C 充值请求并发起下单。 */
    @Override
    public ChannelResult createPayOrder(PaymentOrder order, Channel channel) {
        PayOrderReq request = new PayOrderReq(order,channel,serviceCode);
        U2CResponse<PayRes> response = execute(paymentApi.createPayment(channel.getPayUrl(), request));
        return response.isSuccess() && response.getData() != null ? response.getData().buildPaymentResult() : toFailureResult(response);
    }

    /** 使用本地商户订单号查询 U2C 充值状态。 */
    @Override
    public ChannelResult queryPayOrder(String merchantOrderNo, Channel channel) {
        QueryOrderReq request = new QueryOrderReq(channel.getAppKey(), merchantOrderNo, null);
        request.setSign(PaymentUtils.sign(request, channel.getApiSecret()));
        U2CResponse<PayRes> response = execute(paymentApi.queryPayment(channel.getPaySearchUrl(),request));
        return response.isSuccess() && response.getData() != null ? response.getData().buildPaymentResult() : toFailureResult(response);
    }

    /** 组装 PIX 收款信息并发起提现。 */
    @Override
    public ChannelResult createCashOutOrder(CashOutOrder order, Channel channel, BankCard bankCard) {
        CashOutOrderReq cashOutOrderReq = new CashOutOrderReq(order, channel, bankCard, serviceCode);
        U2CResponse<CashOutRes> response = execute(paymentApi.createCashOut(channel.getCashOutUrl(),cashOutOrderReq));
        return response.isSuccess() && response.getData() != null ? response.getData().buildCashOutResult() : toFailureResult(response);
    }

    /** 查询 U2C 提现状态。 */
    @Override
    public ChannelResult queryCashOutOrder(String merchantOrderNo, Channel channel) {
        QueryOrderReq request = new QueryOrderReq(channel.getAppKey(), merchantOrderNo, null);
        request.setSign(PaymentUtils.sign(request, channel.getApiSecret()));
        U2CResponse<CashOutRes> response = execute(paymentApi.queryCashOut(channel.getCashOutSearchUrl(),request));
        return response.isSuccess() && response.getData() != null ? response.getData().buildCashOutResult() : toFailureResult(response);
    }

    @Override
    public List<BalanceRes> queryBalance(Channel channel) {
        QueryBalanceReq request = new QueryBalanceReq();
        request.setMerchantId(channel.getAppKey());
        request.setSign(PaymentUtils.sign(request, channel.getApiSecret()));
        U2CResponse<List<BalanceRes>> response = execute(paymentApi.queryBalance(channel.getBalanceQueryUrl(), request));
        if (!response.isSuccess() || response.getData() == null) {
            // 当前余额接口只返回 List，不能把调用失败伪装成“余额为零”。
            throw new IllegalStateException(response.getMessage());
        }
        return response.getData();
    }

    @Override
    public ChannelResult parsePaymentCallback(Object paymentCallBack) {
        return parseVerifiedCallback(paymentCallBack);
    }

    @Override
    public ChannelResult parseCashOutCallback(Object cashOutCallBack) {
        return parseVerifiedCallback(cashOutCallBack);
    }

    /**
     * 回调必须先验签再转换业务结果，避免未经验证的数据进入订单状态机。
     */
    private ChannelResult parseVerifiedCallback(Object callBackData) {
        if (callBackData == null) {
            throw new IllegalArgumentException("callback payload or payment channel is missing");
        }
        CallBackRes callBackResult = JSON.parseObject(JSON.toJSONString(callBackData), CallBackRes.class);
        Integer tenantId = Integer.valueOf(callBackResult.getMerchantOrderNo().split("_")[1]);
        Integer siteId = Integer.valueOf(callBackResult.getMerchantOrderNo().split("_")[2]);
        DynamicTableNameHandler.setTenantId(tenantId);
        Channel channel = getPaymentChannel(siteId,callBackResult.getMerchantId());
        if (channel.getWhiteIps() != null && !channel.getWhiteIps().contains(RequestUtils.getRequestIp())) {//已设置白名单，但回调方未处于白名单范围
            return ChannelResult.failed(I18nUtil.getI8nMsg("channelCallBack.error.whiteIps.verify.failed"));
        }
        String callBackSign = callBackResult.getSign();
        String verifySign  = callBackResult.buildSign(channel.getApiSecret());
        if (!callBackSign.equals(verifySign)) {//验证码校验失败
            log.error("U2C 支付回调处理失败，验签异常；返回报文为{}",JSON.toJSONString(callBackData));
            return ChannelResult.failed(I18nUtil.getI8nMsg("channelCallBack.error.sign.verify.failed"));
        }
        return callBackResult.buildPaymentResult(channel.getAmtUnit());
    }

    /**
     * 同步执行 Retrofit 请求，将 HTTP、网络及 U2C 业务错误转换成失败响应。
     * 订单调用方据此保留当前状态，避免一次临时故障中断整批状态同步。
     */
    private <T> U2CResponse<T> execute(Call<U2CResponse<T>> call) {
        try {
            Response<U2CResponse<T>> httpResponse = call.execute();
            U2CResponse<T> response = httpResponse.body();
            if (!httpResponse.isSuccessful()) {
                return failure("HTTP_" + httpResponse.code(), "U2C HTTP error: " + httpResponse.code(),
                        httpResponse.code() >= 500);
            }
            if (response == null) {
                return failure("EMPTY_RESPONSE", "empty U2C response", true);
            }
            return response;
        } catch (IOException ex) {
            return failure("NETWORK_ERROR", "U2C network request failed", true);
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
}
