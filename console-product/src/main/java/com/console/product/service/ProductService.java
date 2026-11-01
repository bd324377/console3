package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {
    Product getProductById(Integer productId);
    List<Product> getProductList(Integer tenantId);
}
