package com.console.framework.utils;

import com.console.framework.config.DynamicTableNameHandler;

public class TenantUtils {
    /**
     * 获取当前租户标识
     */
    public static Integer getTenantId() {
        Integer tenantId = DynamicTableNameHandler.getTenantId();
        if (tenantId == null) {
            tenantId = RequestUtils.getRequestTenantId();
            if (tenantId == null) {
                tenantId = 0;
            }
        }
        return tenantId;
    }
}
