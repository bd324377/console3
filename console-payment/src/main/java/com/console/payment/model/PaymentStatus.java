package com.console.payment.model;

public final class PaymentStatus {
    public static final int PENDING = 1;
    public static final int PAYING = 2;
    public static final int SUCCESS = 3;
    public static final int FAILED = 4;
    public static final int CANCELLED = 5;
    public static final int REFUNDED = 8;

    private PaymentStatus() { }

    public static boolean terminal(Integer status) {
        return status != null && (status == SUCCESS || status == FAILED || status == CANCELLED || status == REFUNDED);
    }
}
