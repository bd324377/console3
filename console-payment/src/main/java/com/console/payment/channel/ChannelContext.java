package com.console.payment.channel;

import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class ChannelContext {
    private static ChannelFactory factory;

    @Resource
    private ChannelFactory channelFactory;

    @PostConstruct
    public void init() {
        factory = channelFactory;
    }

    /**
     * 创建支付订单
     * @param paymentOrder 支付订单信息
     * @return 创建支付订单结果
     */
    public static ChannelResult createPayOrder(PaymentOrder paymentOrder, Channel channel) {
        return factory.getStrategy(channel,false).createPayOrder(paymentOrder,channel);
    }

    /**
     * 查询支付订单
     * @param merchantOrderNo 商户订单号
     * @return 查询支付订单结果
     */
    public static ChannelResult queryPayOrder(Integer channelId, String merchantOrderNo) {
        Channel channel = factory.getChannelById(channelId);
        return factory.getStrategy(channel,false).queryPayOrder(merchantOrderNo,channel);
    }

    /**
     * 创建提现订单
     * @param cashOutOrder 创建提现订单参数
     * @param channel 提现渠道
     * @param bankCard 银行卡信息
     * @return 创建提现订单结果
     */
    public static ChannelResult createCashOutOrder(CashOutOrder cashOutOrder, Channel channel, BankCard bankCard) {
        return factory.getStrategy(channel,true).createCashOutOrder(cashOutOrder,channel,bankCard);
    }

    /**
     * 查询提现订单
     * @param channelId 渠道id
     * @param merchantOrderNo 商户订单号
     * @return 查询提现订单结果
     */
    public static ChannelResult queryCashOutOrder(Integer channelId, String merchantOrderNo) {
        Channel channel = factory.getChannelById(channelId);
        return factory.getStrategy(channel,true).queryCashOutOrder(merchantOrderNo,channel);
    }

    /**
     * 获取渠道余额
     * @param channelId 渠道id
     * @return 渠道余额
     */
    public static Object queryBalance(Integer channelId) {
        Channel channel = factory.getChannelById(channelId);
        return factory.getStrategy(channel,false).queryBalance(channel);
    }

    /**
     * 根据回调信息构建支付结果
     * @param channelEnum 渠道枚举
     * @param paymentCallBack 回调信息
     * @return 支付结果
     */
    public static ChannelResult buildPaymentResultByCallBack(ChannelEnum channelEnum, Object paymentCallBack) {
        return factory.getStrategy(channelEnum).parsePaymentCallback(paymentCallBack);
    }

    /**
     * 支付回调处理
     * @param channelEnum 渠道枚举
     * @param channelResult 回调结果
     */
    public static boolean payCallBackHandle(ChannelEnum channelEnum,ChannelResult channelResult) {
        return factory.getStrategy(channelEnum).payCallBackHandle(channelResult);
    }

    /**
     * 根据回调信息构建提现结果
     * @param channelEnum 渠道枚举
     * @param cashOutCallBack 回调信息
     * @return 提现结果
     */
    public static ChannelResult buildCashOutResultByCallBack(ChannelEnum channelEnum, Object cashOutCallBack) {
        return factory.getStrategy(channelEnum).parseCashOutCallback(cashOutCallBack);
    }

    /**
     * 支付回调处理
     * @param channelEnum 渠道枚举
     * @param channelResult 回调结果
     */
    public static boolean cashOutCallBackHandle(ChannelEnum channelEnum,ChannelResult channelResult) {
        return factory.getStrategy(channelEnum).cashOutCallBackHandle(channelResult);
    }
}
