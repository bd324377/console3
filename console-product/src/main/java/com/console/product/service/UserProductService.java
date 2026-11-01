package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.entity.UserProduct;

import java.util.List;

public interface UserProductService extends IService<UserProduct> {
    void addUserProduct(Integer productId,Integer tenantId,Integer userId,UserProduct.Type type);
    void removeUserProduct(Integer productId,UserProduct.Type type);
    List<Integer> getHistoryProductIdList();
    List<Integer> getCollectProductIdList();
}
