package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TenantUtils;
import com.console.product.model.req.ProductReq;
import com.console.product.entity.Product;
import com.console.product.entity.ProductPlatform;
import com.console.product.mapper.ProductMapper;
import com.console.product.service.ProductPlatformService;
import com.console.product.service.ProductService;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import com.github.xiaolyuh.annotation.Caching;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl extends BaseServiceImpl<ProductMapper, Product> implements ProductService {
    private static final String UPLOAD_PATH = "/uploads/";
    @Value("${console.serviceCode}")
    private String serviceCode;
    @Value("${console.fileUpload.baseFilePath}")
    private String baseFilePath;
    @Resource
    private ProductPlatformService productPlatformService;

    @Override
    @Caching(evict = {
        @CacheEvict(value = CacheConstants.PRODUCT_LIST, key = "T(com.console.framework.utils.TenantUtils).getTenantId()"),
        @CacheEvict(value = CacheConstants.PRODUCT, key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #updateProductReq.id")
    })
    public boolean updateProduct(String field, ProductReq.UpdateProductReq updateProductReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(field)).lambda().eq(Product::getId,updateProductReq.getId());
        Product product = this.getOne(queryWrapper);
        updateProductReq.buildUpdateParam(field,product,requestBackendUser);
        if (this.update(updateProductReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateProductReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    public IPage<Product> getProductPage(Page<Product> page, ProductReq.SearchProductReq searchProductReq) {
        List<ProductPlatform> productPlatformList = productPlatformService.getProductPlatformList(TenantUtils.getTenantId());
        List<Integer> productPlatformIdList = productPlatformList.stream().map(ProductPlatform::getId).toList();
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(Product::getPlatformId,productPlatformIdList)
                .eq(searchProductReq.getPlatformId() != null,Product::getPlatformId,searchProductReq.getPlatformId())
                .like(searchProductReq.getProductCode() != null,Product::getProductCode,searchProductReq.getProductCode())
                .like(searchProductReq.getProductName() != null,Product::getProductName,searchProductReq.getProductName())
                .eq(searchProductReq.getStatus() != null,Product::getStatus,searchProductReq.getStatus());
        IPage<Product> productIPage = this.page(page,queryWrapper);
        for (Product product : productIPage.getRecords()) {
            product.buildIconData(serviceCode,UPLOAD_PATH,baseFilePath);
        }
        return productIPage;
    }

    /**
     * 通过产品ID获取产品详情
     * @param productId 产品ID
     */
    @Override
    @Cacheable(value = CacheConstants.PRODUCT,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #productId")
    public Product getProductById(Integer productId) {
        return this.getById(productId);
    }

    @Override
    @Cacheable(value = CacheConstants.PRODUCT_LIST,key = "#tenantId")
    public List<Product> getProductList(Integer tenantId) {
        //先查出当前启用的产商
        List<ProductPlatform> productPlatformList = productPlatformService.getProductPlatformList(tenantId);
        List<Integer> productPlatformIdList = productPlatformList.stream().map(ProductPlatform::getId).toList();
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,platform_id,category,product_name")
                .lambda().in(Product::getPlatformId,productPlatformIdList)
                .eq(Product::getStatus,1).orderByDesc(Product::getSort);
        List<Product> productList = this.list(queryWrapper);
        if (!ObjectUtils.isEmpty(productList)) {
            for (Product product : productList) {
                product.buildIconData(serviceCode,UPLOAD_PATH,baseFilePath);
            }
//            productList = productList.stream().filter(item -> StringUtils.hasText(item.getIconPath())).toList();
        }
        return productList;
    }
}
