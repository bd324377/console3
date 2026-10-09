package com.console.payment.channel.strategy.fourz;

import com.alibaba.fastjson.JSON;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.ChannelStrategy;
import com.console.payment.channel.strategy.fourz.req.*;
import com.console.payment.channel.strategy.fourz.res.*;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;
import retrofit2.Response;
import java.io.IOException;
import java.util.List;

/** FourZ 只负责协议和远程请求，订单及资金事务沿用公共业务入口。 */
@Slf4j
@Service("fourZPay")
public class FourZStrategy extends ChannelStrategy {
    @Resource
    private FourZPaymentApi paymentApi;

    @Override
    public ChannelResult createPayOrder(PaymentOrder order, Channel channel) {
        PayOrderReq request = new PayOrderReq(order, channel,serviceCode);
        FourZResponse<PayRes> response = execute(paymentApi.createPayment(channel.getPayUrl(), request));
        if (response.isSuccess()) {
            return response.getData().buildPaymentResult();
        }
        return ChannelResult.failed(response.getMessage());
    }

    @Override
    public ChannelResult queryPayOrder(String merchantOrderNo, Channel channel) {
        QueryOrderReq request = new QueryOrderReq(channel.getAppKey(), merchantOrderNo, channel.getApiSecret());
        FourZResponse<PayRes> response = execute(paymentApi.queryPayment(channel.getPaySearchUrl(), request));
        if (response.isSuccess()) {//成功
            return response.getData().buildPaymentResult();
        }
        return ChannelResult.failed(response.getMessage());
    }

    @Override
    public ChannelResult createCashOutOrder(CashOutOrder cashOutOrder, Channel channel, BankCard bankCard) {
        CashOutOrderReq cashOutOrderReq = new CashOutOrderReq(cashOutOrder, channel, bankCard, serviceCode);
        FourZResponse<CashOutRes> response = execute(paymentApi.createCashOut(channel.getCashOutUrl(),cashOutOrderReq));
        return response.isSuccess() && response.getData() != null ? response.getData().buildCashOutResult() : ChannelResult.failed(response.getMessage());
    }

    @Override
    public ChannelResult queryCashOutOrder(String merchantOrderNo, Channel channel) {
        QueryOrderReq cashOutOrderReq = new QueryOrderReq(channel.getAppKey(), merchantOrderNo, channel.getApiSecret());
        FourZResponse<CashOutRes> response = execute(paymentApi.queryCashOut(channel.getCashOutSearchUrl(),cashOutOrderReq));
        return response.isSuccess() && response.getData() != null ? response.getData().buildCashOutResult() : ChannelResult.failed(response.getMessage());
    }

    @Override
    public List<BalanceRes> queryBalance(Channel channel) {
        QueryBalanceReq queryBalanceReq = new QueryBalanceReq(channel.getAppKey(), channel.getApiSecret());
        FourZResponse<List<BalanceRes>> response = execute(paymentApi.queryBalance(channel.getBalanceQueryUrl(), queryBalanceReq));
        if (!response.isSuccess() || ObjectUtils.isEmpty(response.getData())) {
            throw new IllegalStateException("FourZ balance query was not confirmed");
        }
        return response.getData();
    }

    @Override
    public ChannelResult parsePaymentCallback(Object callback) {
        return parseVerifiedCallback(callback);
    }

    @Override
    public ChannelResult parseCashOutCallback(Object callback) {
        return parseVerifiedCallback(callback);
    }

    private ChannelResult parseVerifiedCallback(Object callBackData) {
        if (callBackData == null) {
            throw new IllegalArgumentException("callback data or channel is missing");
        }
        CallBackRes callBackResult = JSON.parseObject(JSON.toJSONString(callBackData), CallBackRes.class);
        Integer tenantId = Integer.valueOf(callBackResult.getMerchantOrderNo().split("_")[1]);
        Integer siteId = Integer.valueOf(callBackResult.getMerchantOrderNo().split("_")[2]);
        DynamicTableNameHandler.setTenantId(tenantId);
        Channel channel = getPaymentChannel(siteId,callBackResult.getMerchantNo());
        if (channel.getWhiteIps() != null && !channel.getWhiteIps().contains(RequestUtils.getRequestIp())) {//已设置白名单，但回调方未处于白名单范围
            return ChannelResult.failed(I18nUtil.getI8nMsg("channelCallBack.error.whiteIps.verify.failed"));
        }
        String callBackSign = callBackResult.getSign();
        String verifySign   = callBackResult.buildSign(channel.getApiSecret());
        if (!callBackSign.equals(verifySign)) {//验证码校验失败
            log.error("four z 支付回调处理失败，验签异常；返回报文为{}",JSON.toJSONString(callBackData));
            return ChannelResult.failed(I18nUtil.getI8nMsg("channelCallBack.error.sign.verify.failed"));
        }
        return callBackResult.buildPaymentResult(channel.getAmtUnit());
    }

    private <T> FourZResponse<T> execute(Call<FourZResponse<T>> call) {
        try {
            Response<FourZResponse<T>> httpResponse = call.execute();
            FourZResponse<T> response = httpResponse.body();
            if (response == null || (response.isSuccess() && response.getData() == null)) {
                return failedResponse("EMPTY_RESPONSE");
            }
            return response;
        } catch (IOException ex) {
            log.warn("FourZ request unconfirmed, error=NETWORK_ERROR");
            return failedResponse("NETWORK_ERROR");
        } catch (RuntimeException ex) {
            log.warn("FourZ request unconfirmed, error=INVALID_RESPONSE");
            return failedResponse("INVALID_RESPONSE");
        }
    }

    private <T> FourZResponse<T> failedResponse(String code) {
        FourZResponse<T> response = new FourZResponse<>();
        response.setErrorCode(code);
        response.setMessage("FourZ request was not confirmed");
        return response;
    }
}
