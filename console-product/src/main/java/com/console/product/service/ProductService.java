package com.console.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.model.req.ProductReq;
import com.console.product.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {
    boolean updateProduct(String field, ProductReq.UpdateProductReq updateProductReq);
    IPage<Product> getProductPage(Page<Product> page, ProductReq.SearchProductReq searchProductReq);
    Product getProductById(Integer productId);
    List<Product> getProductList(Integer tenantId);
}
