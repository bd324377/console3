package com.console.payment.service;

import com.console.payment.channel.strategy.u2c.dto.res.BalanceRes;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.model.req.CreateCashOutReq;
import com.console.payment.model.req.CreatePaymentReq;

import java.util.List;
import java.util.Map;

public interface PaymentApplicationService {
    PaymentOrder createPayment(CreatePaymentReq request, String clientIp);
    PaymentOrder queryPayment(String merchantOrderNo);
    CashOutOrder createCashOut(CreateCashOutReq request);
    CashOutOrder queryCashOut(String merchantOrderNo);
    List<BalanceRes> queryBalance(Long channelId);
    void handlePaymentCallback(Map<String, Object> payload, String clientIp);
    void handleCashOutCallback(Map<String, Object> payload, String clientIp);
    default void onPaymentSucceeded(PaymentOrder order) { }
    default void onCashOutSucceeded(CashOutOrder order) { }
}
