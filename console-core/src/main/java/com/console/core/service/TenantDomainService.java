package com.console.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.Tenant;
import com.console.core.entity.TenantDomain;

public interface TenantDomainService extends IService<TenantDomain> {
    void addTenantDomain(TenantDomain tenantDomain);
    void updateTenantDomain(TenantDomain updateParam);
    IPage<TenantDomain> getTenantPageByTenantId(Page<TenantDomain> page, TenantDomain param);
    Tenant getTenantByDomain(String domain);
}
