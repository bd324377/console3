package com.console.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.dto.ProductTypeReq;
import com.console.product.entity.ProductType;

import java.util.List;

public interface ProductTypeService extends IService<ProductType> {
    boolean addProductType(ProductTypeReq.AddProductTypeReq addProductTypeReq);
    boolean delProductType(ProductTypeReq.DelProductTypeReq delProductTypeReq);
    boolean updateProductType(String part, ProductTypeReq.UpdateProductTypeReq updateProductTypeReq);
    IPage<ProductType> getProductTypePage(Page<ProductType> page, ProductTypeReq.SearchProductTypeReq searchProductTypeReq);
    List<ProductType> getProductTypeList(Integer tenantId, Integer marketSiteId);
}
