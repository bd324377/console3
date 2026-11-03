package com.console.payment.channel.strategy.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.console.framework.domain.BusinessException;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.ChannelStrategy;
import com.console.payment.channel.strategy.test.res.TestCallBackRes;
import com.console.payment.channel.strategy.u2c.res.*;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service("testPay")
public class TestStrategy extends ChannelStrategy {
    /** 组装 U2C 充值请求并发起下单。 */
    @Override
    public ChannelResult createPayOrder(PaymentOrder order, Channel channel) {
        ChannelResult channelResult = new ChannelResult();
        channelResult.setSuccess(true);
        channelResult.setStatus(2);
        channelResult.setOrderNo(IdWorker.getIdStr());
        channelResult.setPayUrl("https://www.baidu.com/");
        channelResult.setMerchantOrderNo(order.getMerchantOrderNo());
        String[] parts = order.getMerchantOrderNo().split("_");
        channelResult.setTenantId(Integer.valueOf(parts[1]));
        return channelResult;
    }

    /** 使用本地商户订单号查询 U2C 充值状态。 */
    @Override
    public ChannelResult queryPayOrder(String merchantOrderNo, Channel channel) {
        return null;
    }

    /** 组装 PIX 收款信息并发起提现。 */
    @Override
    public ChannelResult createCashOutOrder(CashOutOrder order, Channel channel, BankCard bankCard) {
        ChannelResult channelResult = new ChannelResult();
        channelResult.setSuccess(true);
        channelResult.setStatus(2);
        channelResult.setOrderNo(IdWorker.getIdStr());
        channelResult.setMerchantOrderNo(order.getMerchantOrderNo());
        String[] parts = order.getMerchantOrderNo().split("_");
        channelResult.setTenantId(Integer.valueOf(parts[1]));
        return channelResult;
    }

    /** 查询 U2C 提现状态。 */
    @Override
    public ChannelResult queryCashOutOrder(String merchantOrderNo, Channel channel) {
        return null;
    }

    @Override
    public List<BalanceRes> queryBalance(Channel channel) {
        return null;
    }

    @Override
    public ChannelResult parsePaymentCallback(Object paymentCallBack) {
        return parseVerifiedCallback(paymentCallBack);
    }

    @Override
    public ChannelResult parseCashOutCallback(Object cashOutCallBack) {
        return parseVerifiedCallback(cashOutCallBack);
    }

    private ChannelResult parseVerifiedCallback(Object callBackData) {
        if (callBackData == null) {
            throw new IllegalArgumentException("callback payload or payment channel is missing");
        }
        TestCallBackRes testCallBackRes = JSON.parseObject(JSON.toJSONString(callBackData),TestCallBackRes.class);
        if (!testCallBackRes.getSign().equals("临兵斗者皆数组前行")) {
            throw new BusinessException(500,"签名错误");
        }

        ChannelResult channelResult = new ChannelResult();
        channelResult.setMerchantOrderNo(testCallBackRes.getMerchantOrderNo());
        channelResult.setAmount(Integer.parseInt(testCallBackRes.getAmount()));
        channelResult.setStatus(0);
        String[] parts = testCallBackRes.getMerchantOrderNo().split("_");
        channelResult.setTenantId(Integer.valueOf(parts[1]));
        channelResult.setAmtUnit(Channel.AmtUnit.FEN);
        channelResult.setSuccess(true);
        return channelResult;
    }
}
