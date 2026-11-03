package com.console.payment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.model.PaymentOrderReq;

public interface PaymentOrderService extends IService<PaymentOrder> {
    boolean createPaymentOrder(PaymentOrderReq.CreatePaymentOrderReq createPaymentOrderReq);
    IPage<PaymentOrder> getPaymentOrderPage(Page<PaymentOrder> page, PaymentOrderReq.SearchPaymentOrderReq searchPaymentOrderReq);
    boolean handlePaymentOrder(PaymentOrder order, ChannelResult result);
}
