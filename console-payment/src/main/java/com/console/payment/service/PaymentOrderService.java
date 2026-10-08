package com.console.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.PaymentOrder;

public interface PaymentOrderService extends IService<PaymentOrder> {

    /**
     * 处理已通过验签和一致性校验的充值回调。
     *
     * @return 本次回调是否实际更新了订单
     */
    boolean handlePaymentOrder(PaymentOrder order, ChannelResult result);
}
