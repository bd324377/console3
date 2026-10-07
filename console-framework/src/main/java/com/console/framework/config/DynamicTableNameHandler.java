package com.console.framework.config;

public class DynamicTableNameHandler {
    //每个请求线程维护一个数据，避免多线程数据冲突。所以使用ThreadLocal
    private static final ThreadLocal<Integer> tenant = new ThreadLocal<>();
    //设置请求线程的租户编码数据
    public static void setTenantId(Integer tenantId) {
        tenant.set(tenantId);
    }

    public static Integer getTenantId() {
        return tenant.get();
    }

    public static void removeTenantId() {
        tenant.remove();
    }
}
