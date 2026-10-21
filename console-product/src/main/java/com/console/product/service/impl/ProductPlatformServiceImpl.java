package com.console.product.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.product.entity.ProductPlatform;
import com.console.product.mapper.ProductPlatformMapper;
import com.console.product.service.ProductPlatformService;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductPlatformServiceImpl extends BaseServiceImpl<ProductPlatformMapper,ProductPlatform> implements ProductPlatformService {

    @Override
    @Cacheable(value = CacheConstants.PRODUCT_PLATFORM,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #platformId")
    public ProductPlatform getProductPlatformById(Integer platformId) {
        return this.getById(platformId);
    }
}
