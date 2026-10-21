package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.entity.ProductPlatform;

public interface ProductPlatformService extends IService<ProductPlatform> {
    ProductPlatform getProductPlatformById(Integer platformId);
}
