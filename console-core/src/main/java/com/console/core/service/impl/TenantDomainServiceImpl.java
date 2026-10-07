package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.core.entity.Tenant;
import com.console.core.entity.TenantDomain;
import com.console.core.mapper.TenantDomainMapper;
import com.console.core.service.TenantDomainService;
import com.console.core.service.TenantService;
import com.console.framework.constants.CacheConstants;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TenantDomainServiceImpl extends ServiceImpl<TenantDomainMapper, TenantDomain> implements TenantDomainService {
    @Resource
    private TenantService tenantService;
    @Override
    @Cacheable(value = CacheConstants.TENANT_DOMAIN,key = "#domain")
    public Tenant getTenantByDomain(String domain) {
        QueryWrapper<TenantDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TenantDomain::getDomain,domain);
        TenantDomain tenantDomain = this.getOne(queryWrapper);
        if (tenantDomain != null) {
            return tenantService.getTenantById(tenantDomain.getTenantId());
        }
        return null;
    }
}
