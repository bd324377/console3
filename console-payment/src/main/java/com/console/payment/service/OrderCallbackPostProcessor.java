package com.console.payment.service;

import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentOrder;

/**
 * 订单首次进入终态后的扩展点，后续可在这里接入钱包和资金流水。
 */
public interface OrderCallbackPostProcessor {

    default void afterPaymentCompleted(PaymentOrder order) {
    }

    default void afterCashOutCompleted(CashOutOrder order) {
    }
}
