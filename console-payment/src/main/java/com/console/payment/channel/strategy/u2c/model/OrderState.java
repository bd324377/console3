package com.console.payment.channel.strategy.u2c.model;

import lombok.Getter;

/**
 * 支付/提现订单状态
 */
@Getter
public enum OrderState {
    PAID(0,"PAID"),//支付成功
    WAITING_PAY(1,"WAITING_PAY"),//待支付
    PAYING(2,"PAYING"),//支付中
    PAY_FAILED(3,"PAY_FAILED"),//支付失败
    REFUND(4,"REFUND");//已退款

    private final Integer key;
    private final String  value;

    OrderState(int key, String value) {
        this.key   = key;
        this.value = value;
    }

    /**
     * 根据状态值获取订单状态
     * @param value 状态值
     */
    public static OrderState getOrderStateByValue(String value) {
        for (OrderState orderState : values()) {
            if (orderState.getValue().equals(value)) {
                return orderState;
            }
        }
        return OrderState.WAITING_PAY;
    }
}
