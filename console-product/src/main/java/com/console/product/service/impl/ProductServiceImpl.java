package com.console.product.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.product.entity.Product;
import com.console.product.mapper.ProductMapper;
import com.console.product.service.ProductService;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends BaseServiceImpl<ProductMapper, Product> implements ProductService {

    /**
     * 通过产品ID获取产品详情
     * @param productId 产品ID
     */
    @Override
    @Cacheable(value = CacheConstants.PRODUCT,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #productId")
    public Product getProductById(Integer productId) {
        return this.getById(productId);
    }
}
