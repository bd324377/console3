package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.product.entity.PlatformBelong;
import com.console.product.entity.Product;
import com.console.product.entity.ProductPlatform;
import com.console.product.mapper.ProductMapper;
import com.console.product.service.PlatformBelongService;
import com.console.product.service.ProductPlatformService;
import com.console.product.service.ProductService;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
        queryWrapper.select("id,platform_id,category,product_code,product_name")
                .lambda().in(Product::getPlatformId,productPlatformIdList)
                .eq(Product::getStatus,1).orderByDesc(Product::getSort);
        List<Product> productList = this.list(queryWrapper);
        if (!ObjectUtils.isEmpty(productList)) {
            for (Product product : productList) {
                product.buildIconData(serviceCode,UPLOAD_PATH,baseFilePath);
            }
            productList = productList.stream().filter(item -> StringUtils.hasText(item.getIconPath())).toList();
        }
        return productList;
    }
}
