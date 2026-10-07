package com.console.payment.model;

public final class CashOutStatus {
    public static final int PENDING = 1;
    public static final int APPROVED = 2;
    public static final int REJECTED = 3;
    public static final int PAYING = 4;
    public static final int SUCCESS = 5;
    public static final int FAILED = 6;
    public static final int CANCELLED = 7;
    public static final int REFUNDED = 8;
    public static final int TIMEOUT = 9;

    private CashOutStatus() { }

    public static boolean terminal(Integer status) {
        return status != null && (status == REJECTED || status == SUCCESS || status == FAILED
                || status == CANCELLED || status == REFUNDED || status == TIMEOUT);
    }
}
