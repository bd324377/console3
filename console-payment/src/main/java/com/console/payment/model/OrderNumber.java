package com.console.payment.model;

/** 商户订单号格式及解析逻辑：PAY/CASH_租户ID_本地订单ID。 */
public record OrderNumber(String type, int tenantId, long orderId) {
    public static OrderNumber parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("merchant order number is missing");
        }
        String[] parts = value.split("_");
        if (parts.length != 3 || !("PAY".equals(parts[0]) || "CASH".equals(parts[0]))) {
            throw new IllegalArgumentException("invalid merchant order number");
        }
        try {
            return new OrderNumber(parts[0], Integer.parseInt(parts[1]), Long.parseLong(parts[2]));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid merchant order number", ex);
        }
    }

    public static String payment(int tenantId, long orderId) {
        return "PAY_" + tenantId + "_" + orderId;
    }

    public static String cashOut(int tenantId, long orderId) {
        return "CASH_" + tenantId + "_" + orderId;
    }
}
