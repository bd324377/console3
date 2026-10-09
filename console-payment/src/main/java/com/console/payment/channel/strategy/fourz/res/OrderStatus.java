package com.console.payment.channel.strategy.fourz.res;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PAID(0,"PAID"),//支付成功
    WAITING_PAY(1,"WAITING_PAY"),//待支付
    PAYING(2,"PAYING"),//支付中
    PAY_FAILED(3,"PAY_FAILED"),//支付失败
    REFUND(4,"REFUND");//已退款

    private final Integer key;
    private final String  value;

    OrderStatus(int key, String value) {
        this.key   = key;
        this.value = value;
    }

    /**
     * 根据状态值获取订单状态
     * @param value 状态值
     */
    public static OrderStatus getOrderStatusByValue(String value) {
        for (OrderStatus orderStatus : values()) {
            if (orderStatus.getValue().equals(value)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("unsupported channel status");
    }
}
