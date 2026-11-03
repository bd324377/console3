package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.admin.entity.CustomerCare;
import com.console.admin.mapper.CustomerCareMapper;
import com.console.admin.model.req.CustomerCareReq;
import com.console.admin.service.CustomerCareService;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RedisUtils;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CustomerCareServiceImpl extends BaseServiceImpl<CustomerCareMapper, CustomerCare> implements CustomerCareService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCustomerCare(CustomerCareReq.AddCustomerCareReq addCustomerCareReq) {
        CustomerCare customerCare = new CustomerCare();
        BeanUtils.copyProperties(addCustomerCareReq,customerCare);
        if (this.save(customerCare)) {
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.CUSTOMER_CARE, AdminProc.ProcType.ADD, customerCare.getId(),null,customerCare);
            adminProcMapper.insert(adminProc);
            clearCustomerCareCache(requestBackendUser,addCustomerCareReq.getMarketSiteId(), addCustomerCareReq.getMarketTeamId(), addCustomerCareReq.getMarketerId());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delCustomerCare(Integer customerCareId) {
        CustomerCare customerCare = this.getById(customerCareId);
        if (customerCare == null) {
            throw new BusinessException(500,"删除失败，未在系统中找到该客服信息");
        }
        if (this.removeById(customerCareId)) {
            RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.CUSTOMER_CARE, AdminProc.ProcType.DELETE, customerCareId,customerCare,null);
            adminProcMapper.insert(adminProc);
            clearCustomerCareCache(requestBackendUser,customerCare.getMarketSiteId(), customerCare.getMarketTeamId(), customerCare.getMarketerId());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCustomerCare(String field, CustomerCareReq.UpdateCustomerCareReq updateCustomerCareReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<CustomerCare> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,market_site_id,market_team_id,marketer_id", FieldUtils.convertToUnderscore(field)).lambda().eq(CustomerCare::getId,updateCustomerCareReq.getId());
        CustomerCare customerCare = this.getOne(queryWrapper);
        updateCustomerCareReq.buildUpdateParam(field,customerCare,requestBackendUser);
        if (this.update(updateCustomerCareReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateCustomerCareReq.getAdminProc());
            clearCustomerCareCache(requestBackendUser,customerCare.getMarketSiteId(), customerCare.getMarketTeamId(), customerCare.getMarketerId());
            return true;
        }
        return false;
    }

    @Override
    public IPage<CustomerCare> getCustomerCarePage(IPage<CustomerCare> page) {
        return this.page(page);
    }

    @Override
    @Cacheable(value = CacheConstants.CUSTOMER_CARE_LIST,key = "#tenantId + ':' + #marketSiteId + ':' + #marketTeamId + ':' + #marketerId")
    public List<CustomerCare> getCustomerCareList(Integer tenantId,Integer marketSiteId,Integer marketTeamId,Integer marketerId) {
        QueryWrapper<CustomerCare> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("customer_care_name,icon,route_path,desc")
                .lambda().eq(CustomerCare::getMarketSiteId,marketSiteId)
                .eq(CustomerCare::getMarketTeamId,marketTeamId)
                .eq(CustomerCare::getMarketerId,marketerId);
        List<CustomerCare> customerCareList = this.list(queryWrapper);
        if (ObjectUtils.isEmpty(customerCareList)) {
            customerCareList = getCustomerCareList(tenantId,marketSiteId,marketTeamId);
        }
        return customerCareList;
    }

    @Cacheable(value = CacheConstants.CUSTOMER_CARE_LIST,key = "#tenantId + ':' + #marketSiteId + ':' + #marketTeamId")
    public List<CustomerCare> getCustomerCareList(Integer tenantId,Integer marketSiteId,Integer marketTeamId) {
        QueryWrapper<CustomerCare> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("customer_care_name,icon,route_path,desc")
                .lambda().eq(CustomerCare::getMarketSiteId,marketSiteId)
                .eq(CustomerCare::getMarketTeamId,marketTeamId)
                .isNull(CustomerCare::getMarketerId);
        List<CustomerCare> customerCareList = this.list(queryWrapper);
        if (ObjectUtils.isEmpty(customerCareList)) {
            customerCareList = getCustomerCareList(tenantId,marketSiteId);
        }
        return customerCareList;
    }

    @Cacheable(value = CacheConstants.CUSTOMER_CARE_LIST,key = "#tenantId + ':' + #marketSiteId")
    public List<CustomerCare> getCustomerCareList(Integer tenantId,Integer marketSiteId) {
        QueryWrapper<CustomerCare> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("customer_care_name,icon,route_path,desc")
                .lambda().eq(CustomerCare::getMarketSiteId,marketSiteId)
                .isNull(CustomerCare::getMarketTeamId)
                .isNull(CustomerCare::getMarketerId);
        List<CustomerCare> customerCareList = this.list(queryWrapper);
        if (ObjectUtils.isEmpty(customerCareList)) {
            customerCareList = getCustomerCareList(tenantId);
        }
        return customerCareList;
    }

    @Cacheable(value = CacheConstants.CUSTOMER_CARE_LIST,key = "#tenantId")
    public List<CustomerCare> getCustomerCareList(Integer tenantId) {
        QueryWrapper<CustomerCare> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("customer_care_name,icon,route_path,desc")
                .lambda()
                .isNull(CustomerCare::getMarketSiteId)
                .isNull(CustomerCare::getMarketTeamId)
                .isNull(CustomerCare::getMarketerId);
        return this.list(queryWrapper);
    }

    private void clearCustomerCareCache(RequestBackendUser requestBackendUser,
                                        Integer marketSiteId,Integer marketTeamId,Integer marketerId) {
        String redisKeyPrefix = CacheConstants.CUSTOMER_CARE_LIST + ":" + requestBackendUser.getTenantId();
        if (marketSiteId == null) {
            redisKeyPrefix = redisKeyPrefix + ":*";
        } else if (marketTeamId == null) {
            redisKeyPrefix = redisKeyPrefix + ":" + marketSiteId + ":*";
        } else if (marketerId == null) {
            redisKeyPrefix = redisKeyPrefix + ":" + marketSiteId + ":" + marketTeamId + ":*";
        } else {
            redisKeyPrefix = redisKeyPrefix + ":" + marketSiteId + ":" + marketTeamId + ":" + marketerId;
        }
        RedisUtils.delKeysByPattern(redisKeyPrefix);
    }
}
