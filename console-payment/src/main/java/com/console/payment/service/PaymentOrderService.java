package com.console.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.PaymentOrder;

public interface PaymentOrderService extends IService<PaymentOrder> {
    boolean handlePaymentOrder(PaymentOrder order, ChannelResult result);
}
