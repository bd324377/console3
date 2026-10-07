package com.console.core.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.core.model.dto.req.TenantReq;
import com.console.core.entity.Tenant;
import com.console.core.mapper.AdminProcMapper;
import com.console.core.mapper.TenantMapper;
import com.console.core.service.TenantService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户服务实现类
 */
@Slf4j
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {
    @Resource
    private AdminProcMapper adminProcMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Cacheable(value = CacheConstants.TENANT_LIST)
    public boolean createTenant(TenantReq.CreateTenantReq createTenantReq) {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(createTenantReq,tenant);
        return this.save(tenant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.TENANT,key = "#updateTenantReq.id")
    public boolean updateTenant(String part, TenantReq.UpdateTenantReq updateTenantReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getUser(RequestBackendUser.class);
        DynamicTableNameHandler.setTenantId(0);
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(part))
                .lambda().eq(Tenant::getId,updateTenantReq.getTenantId());
        Tenant tenant = this.getOne(queryWrapper);
        updateTenantReq.buildUpdateParam(part,tenant,requestBackendUser);
        if (StringUtils.hasText(updateTenantReq.getErrorDesc())) {
            throw new BusinessException(500,updateTenantReq.getErrorDesc());
        }
        if (this.update(updateTenantReq.getUpdateWrapper())) {//更新成功，记录操作过程
            DynamicTableNameHandler.setTenantId(requestBackendUser.getTenantId());
            adminProcMapper.insert(updateTenantReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    @Cacheable(value = CacheConstants.TENANT,key = "#id")
    public Tenant getTenantById(Integer id) {
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,tenant_name,layout_id,domains,backend_domains,status,time_zone,white_ips,backend_limit_ips," +
                        "pwa_down_load_domain,web_push_config,web_version,backend_web_version,order_mode,recharge_rate,manual_deposit_limit," +
                        "withdrawal_limit,order_rate")
                .lambda().eq(Tenant::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.TENANT_DOMAIN,key = "#domain")
    public Tenant getTenantByDomain(String domain) {
        List<Tenant> tenantList = getTenantList();
        for (Tenant item : tenantList) {
            if (!ObjectUtils.isEmpty(item.getDomains()) && item.getDomains().contains(domain)) {
                return item;
            }
        }
        return null;
    }

    @Override
    @Cacheable(value = CacheConstants.TENANT_BACKEND_DOMAIN,key = "#domain")
    public Tenant getTenantByBackendDomain(String domain) {
        List<Tenant> tenantList = getTenantList();
        for (Tenant item : tenantList) {
            if (!ObjectUtils.isEmpty(item.getBackendDomains()) && item.getBackendDomains().contains(domain)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Tenant getTenantDetailsById(Integer id) {
        return this.getById(id);
    }

    @Override
    @Cacheable(value = CacheConstants.TENANT_LIST)
    public List<Tenant> getTenantList() {
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,tenant_name,layout_id,domains,backend_domains,status,time_zone,white_ips,backend_limit_ips," +
                "pwa_down_load_domain,web_push_config,web_version,backend_web_version,order_mode,recharge_rate,manual_deposit_limit," +
                "withdrawal_limit,order_rate").lambda().orderByDesc(Tenant::getId);
        return this.list(queryWrapper);
    }

    /**
     * 根据管理后台域名获取租户信息列表（总租户获取全部租户，子租户只获取当前租户信息）
     * @param domain    待查询域名
     */
    @Override
    public List<Tenant> getTenantListByBackendDomain(String domain) {
        List<Tenant> tenantList = new ArrayList<>();
        Tenant tenant = getTenantByBackendDomain(domain);
        if (tenant == null) {
            return null;
        }
        if (tenant.getId() == 0) {//总租户，返回
            tenantList = getTenantList().stream().map(item -> {
                Tenant newTenant = new Tenant();
                newTenant.setId(item.getId());
                newTenant.setTenantName(item.getTenantName());
                newTenant.setTimeZone(item.getTimeZone());
                return newTenant;
            }).toList();
        } else {
            Tenant newTenant = new Tenant();
            newTenant.setId(tenant.getId());
            newTenant.setTenantName(tenant.getTenantName());
            newTenant.setTimeZone(tenant.getTimeZone());
            tenantList.add(newTenant);
        }
        return tenantList;
    }

    @Override
    public IPage<Tenant> getTenantPage(Page<Tenant> page, Tenant tenant) {
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(StringUtils.hasText(tenant.getTenantName()), Tenant::getTenantName, tenant.getTenantName())
                .eq(tenant.getStatus() != null, Tenant::getStatus, tenant.getStatus())
                .eq(tenant.getLayoutId() != null, Tenant::getLayoutId, tenant.getLayoutId())
                .orderByDesc(Tenant::getCreateTime);
        return this.page(page, queryWrapper);
    }
}
