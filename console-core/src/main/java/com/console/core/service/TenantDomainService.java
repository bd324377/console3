package com.console.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.Tenant;
import com.console.core.entity.TenantDomain;

public interface TenantDomainService extends IService<TenantDomain> {
    Tenant getTenantByDomain(String domain);
}
