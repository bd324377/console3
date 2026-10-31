package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.console.product.dto.OrderModeReq;
import com.console.product.entity.OrderMode;
import com.console.product.mapper.OrderModeMapper;
import com.console.product.service.OrderModeService;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrderModeServiceImpl extends BaseServiceImpl<OrderModeMapper, OrderMode> implements OrderModeService {

    @Override
    @Cacheable(value = CacheConstants.ORDER_MODE,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #orderModeId")
    public OrderMode getOrderModeById(Integer orderModeId) {
        return this.getById(orderModeId);
    }


    @Override
    @Cacheable(value = CacheConstants.SYS_ORDER_MODE,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #marketSiteId + ':' + #orderModeType")
    public OrderMode getSysOrderModeByType(Integer marketSiteId,Integer orderModeType) {
        QueryWrapper<OrderMode> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderMode::getModeType,orderModeType).eq(OrderMode::getMarketSiteId,marketSiteId);
        OrderMode orderMode = this.getOne(queryWrapper);
        if (orderMode == null) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(OrderMode::getModeType,orderModeType).last("LIMIT 1");//默认站点 测试
            orderMode = this.getOne(queryWrapper);
        }
        return orderMode;
    }

    @Override
    @CacheEvict(value = {CacheConstants.ORDER_MODE,CacheConstants.SYS_ORDER_MODE},
            key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':'",
            allEntries = true)
    public boolean updateOrderMode(String field, OrderModeReq.UpdateOrderModeReq updateOrderModeReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<OrderMode> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(field)).lambda().eq(OrderMode::getId,updateOrderModeReq.getId());
        OrderMode orderMode = this.getOne(queryWrapper);
        updateOrderModeReq.buildUpdateParam(field,orderMode,requestBackendUser);
        if (this.update(updateOrderModeReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateOrderModeReq.getAdminProc());
            return true;
        }
        return false;
    }
}
