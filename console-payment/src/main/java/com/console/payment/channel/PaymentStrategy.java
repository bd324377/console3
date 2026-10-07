package com.console.payment.channel;

import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.model.CreatePaymentCommand;
import com.console.payment.channel.strategy.u2c.dto.res.BalanceRes;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentChannel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.service.PaymentChannelService;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;

/**
 * 支付渠道统一扩展点。具体渠道只负责协议转换和远程请求，
 * 本地订单持久化、状态机和回调幂等由应用服务统一处理。
 */
public class PaymentStrategy {
    @Resource
    private PaymentChannelService paymentChannelService;

    public ChannelResult createPayment(PaymentOrder order, PaymentChannel channel, String serviceCode) {
        throw unsupported();
    }

    public ChannelResult queryPayment(PaymentOrder order, PaymentChannel channel) {
        throw unsupported();
    }

    public ChannelResult createCashOut(CashOutOrder order, PaymentChannel channel, BankCard bankCard) {
        throw unsupported();
    }

    public ChannelResult queryCashOut(CashOutOrder order, PaymentChannel channel) {
        throw unsupported();
    }

    public List<BalanceRes> queryBalance(PaymentChannel channel) {
        throw unsupported();
    }

    public boolean verifyCallback(Map<String, Object> payload, PaymentChannel channel) {
        throw unsupported();
    }

    public ChannelResult parsePaymentCallback(Object payload, PaymentChannel channel) {
        throw unsupported();
    }

    public ChannelResult parseCashOutCallback(Object payload, PaymentChannel channel) {
        throw unsupported();
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("payment channel operation is not supported");
    }

    public PaymentChannel getPaymentChannel(Integer siteId,String merchantId) {
        return paymentChannelService.getChannelByMerchantId(siteId,merchantId);
    }
}
