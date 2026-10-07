package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.core.entity.Tenant;
import com.console.core.mapper.AdminProcMapper;
import com.console.core.service.TenantService;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;

public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    @Resource
    protected TenantService tenantService;
    @Resource
    protected AdminProcMapper adminProcMapper;

    /**
     * 获取当前请求的租户信息
     */
    protected Tenant getTenantByDomain() {
        return tenantService.getTenantByDomain(RequestUtils.getRequestDomain());
    }


    /**
     * 获取当前请求的租户标识
     */
    protected Integer getTenantIdByDomain() {
        Tenant tenant = tenantService.getTenantByDomain(RequestUtils.getRequestDomain());
        if (tenant != null) {
            return tenant.getId();
        }
        return null;
    }
}
