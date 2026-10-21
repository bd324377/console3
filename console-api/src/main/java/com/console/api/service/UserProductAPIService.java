package com.console.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.ProductOrderDTO;
import com.console.product.entity.ProductOrder;

import java.util.List;

public interface UserProductAPIService {
    void collectProduct(Long productId);//收藏产品
    boolean cancelCollectProduct(Long productId);//取消收藏产品
    List<Long> getHistoryProductIdList();//历史记录
    IPage<ProductOrder> getProductOrderPage(Page<ProductOrder> page, ProductOrderDTO.SearchOrderReq searchOrderDTO);//分页查询产品订单记录
}
