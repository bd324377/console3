package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.admin.entity.LayoutConfig;
import com.console.admin.entity.Media;
import com.console.admin.service.LayoutConfigService;
import com.console.admin.service.MediaService;
import com.console.api.service.LoadAPIService;
import com.console.core.entity.Tenant;
import com.console.core.model.bo.MarketSiteBo;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.RequestUtils;
import com.console.product.entity.PlatformBelong;
import com.console.product.entity.Product;
import com.console.product.entity.ProductPlatform;
import com.console.product.service.PlatformBelongService;
import com.console.product.service.ProductPlatformService;
import com.console.product.service.ProductService;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoadAPIServiceImpl extends BaseServiceImpl implements LoadAPIService {
    @Resource
    private ProductService productService;
    @Resource
    private ProductPlatformService productPlatformService;
    @Resource
    private LayoutConfigService layoutConfigService;
    @Resource
    private MediaService mediaService;

    /** 获取产品信息 **/
    @Override
    public Map<String,Object> getProductInfo() {
        MarketSiteBo marketSiteBo = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        verifySiteValidity(marketSiteBo);//校验租户，站点情况

        List<ProductPlatform> productPlatformList = productPlatformService.getProductPlatformList(marketSiteBo.getTenantId());
        List<Product> productList = productService.getProductList(marketSiteBo.getTenantId());
        Map<String,Object> productInfoMap = new HashMap<>();
        productInfoMap.put("productPlatforms",productPlatformList);
        productInfoMap.put("products",productList);
        return productInfoMap;
    }

    @Override
    public Object getLayoutInfo() {
        MarketSiteBo marketSiteBo = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        verifySiteValidity(marketSiteBo);//校验租户，站点情况
        LayoutConfig layoutConfig = layoutConfigService.getLayoutConfigById(marketSiteBo.getMarketSiteId());
        List<Media> mediaList = mediaService.getMediaList(marketSiteBo.getTenantId(),marketSiteBo.getMarketSiteId());
        Map<Integer,List<Media>> mediaTypeMap = mediaList.stream().collect(Collectors.groupingBy(Media::getMediaType));
        return null;
    }
}
