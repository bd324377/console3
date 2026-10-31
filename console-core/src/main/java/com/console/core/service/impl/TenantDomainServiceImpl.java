package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.core.entity.AdminProc;
import com.console.core.entity.Tenant;
import com.console.core.entity.TenantDomain;
import com.console.core.mapper.AdminProcMapper;
import com.console.core.mapper.TenantDomainMapper;
import com.console.core.service.TenantDomainService;
import com.console.core.service.TenantService;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TenantDomainServiceImpl extends ServiceImpl<TenantDomainMapper, TenantDomain> implements TenantDomainService {
    @Resource
    private TenantService tenantService;
    @Resource
    private AdminProcMapper adminProcMapper;

    @Override
    public void addTenantDomain(TenantDomain tenantDomain) {
        try {
            this.save(tenantDomain);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(500,"绑定域名失败，该域名已绑定其它租户");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantDomain(TenantDomain updateParam) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        TenantDomain tenantDomain = this.getById(updateParam.getId());
        if (tenantDomain == null) {
            throw new BusinessException(500,"修改租户域名失败，该域名配置不存在");
        }
        if (tenantDomain.getDomain().equals(updateParam.getDomain())) {
            throw new BusinessException(500,"修改租户域名失败，修改域名和之前一样，无需修改");
        }
        UpdateWrapper<TenantDomain> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TenantDomain::getId,updateParam.getId()).set(TenantDomain::getDomain,updateParam.getDomain());
        AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.TENANT_DOMAIN, AdminProc.ProcType.UPDATE, updateParam.getId(),tenantDomain.getDomain(),updateParam.getDomain());
        this.update(updateWrapper);
        adminProcMapper.insert(adminProc);
    }

    @Override
    public IPage<TenantDomain> getTenantPageByTenantId(Page<TenantDomain> page, TenantDomain param) {
        QueryWrapper<TenantDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(TenantDomain::getTenantId,param.getTenantId())
                .like(StringUtils.hasText(param.getDomain()),TenantDomain::getDomain,param.getDomain());
        return this.page(page,queryWrapper);
    }

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
