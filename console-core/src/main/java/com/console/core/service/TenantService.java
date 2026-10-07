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

    /**
     * 创建租户
     * @param createTenantReq 租户信息
     * @return 是否成功
     */
    boolean createTenant(TenantReq.CreateTenantReq createTenantReq);

    /**
     * 更新租户
     * @param updateTenantReq 待修改租户信息
     * @return 是否成功
     */
    boolean updateTenant(String part, TenantReq.UpdateTenantReq updateTenantReq);

    /**
     * 根据ID获取租户信息
     * @param id 租户ID
     * @return 租户信息
     */
    Tenant getTenantById(Integer id);
    Tenant getTenantByDomain(String domain);
    Tenant getTenantByBackendDomain(String domain);
    Tenant getTenantDetailsById(Integer id);

    /**
     *
     * @return 租户列表
     */
    List<Tenant> getTenantList();//获取所有租户列表
    List<Tenant> getTenantListByBackendDomain(String domain);

    /**
     * 分页查询租户列表
     * @param page 分页参数
     * @param tenant 查询条件
     * @return 分页结果
     */
    IPage<Tenant> getTenantPage(Page<Tenant> page, Tenant tenant);
}
