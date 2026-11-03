package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.model.req.OrderModeReq;
import com.console.product.entity.OrderMode;

public interface OrderModeService extends IService<OrderMode> {
    OrderMode getOrderModeById(Integer orderModeId);
    OrderMode getSysOrderModeByType(Integer marketSiteId,Integer orderModeType);
    boolean updateOrderMode(String field, OrderModeReq.UpdateOrderModeReq updateOrderModeReq);
}
