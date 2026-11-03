package com.console.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.admin.entity.CustomerCare;
import com.console.admin.model.req.CustomerCareReq;

import java.util.List;

public interface CustomerCareService extends IService<CustomerCare> {
    boolean addCustomerCare(CustomerCareReq.AddCustomerCareReq addCustomerCareReq);
    boolean delCustomerCare(Integer customerCareId);
    boolean updateCustomerCare(String field, CustomerCareReq.UpdateCustomerCareReq updateCustomerCareReq);
    IPage<CustomerCare> getCustomerCarePage(IPage<CustomerCare> page);
    List<CustomerCare> getCustomerCareList(Integer tenantId, Integer marketSiteId, Integer marketTeamId, Integer marketerId);
}
