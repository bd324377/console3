package com.console.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.model.dto.req.TenantReq;
import com.console.core.entity.Tenant;

import java.util.List;

/**
 * 租户服务接口
 */
public interface TenantService extends IService<Tenant> {
    boolean createTenant(TenantReq.CreateTenantReq createTenantReq);//创建新租户
    boolean updateTenant(String part, TenantReq.UpdateTenantReq updateTenantReq);//更新租户信息
    Tenant getTenantById(Integer id);//根据ID获取租户信息
    Tenant getTenantByDomain(String domain);
    Tenant getTenantByBackendDomain(String domain);
    Tenant getTenantDetailsById(Integer id);
    List<Tenant> getTenantList();//获取所有租户列表
    List<Tenant> getTenantListByBackendDomain(String domain);
    IPage<Tenant> getTenantPage(Page<Tenant> page, Tenant tenant);//分页查询租户列表
}
