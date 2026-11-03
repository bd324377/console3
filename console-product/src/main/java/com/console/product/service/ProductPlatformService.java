package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.model.req.ProductPlatformReq;
import com.console.product.entity.ProductPlatform;

import java.util.List;

public interface ProductPlatformService extends IService<ProductPlatform> {
    boolean syncPlatformList();
    boolean updateProductPlatform(String field, ProductPlatformReq.UpdateProductPlatformReq updateProductPlatformReq);
    ProductPlatform getProductPlatformById(Integer platformId);
    List<ProductPlatform> getProductPlatformList(Integer tenantId);
}
