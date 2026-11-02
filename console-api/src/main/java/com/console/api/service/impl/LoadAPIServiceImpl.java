package com.console.api.service.impl;

import com.console.admin.entity.LayoutConfig;
import com.console.admin.entity.Media;
import com.console.admin.entity.ShortcutIcon;
import com.console.admin.service.LayoutConfigService;
import com.console.admin.service.MediaService;
import com.console.admin.service.ShortcutIconService;
import com.console.api.dto.res.LoadLayoutRes;
import com.console.api.service.LoadAPIService;
import com.console.core.model.bo.MarketSiteBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.utils.RequestUtils;
import com.console.product.entity.Product;
import com.console.product.entity.ProductPlatform;
import com.console.product.entity.ProductType;
import com.console.product.service.ProductPlatformService;
import com.console.product.service.ProductService;
import com.console.product.service.ProductTypeService;
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
    @Resource
    private ShortcutIconService shortcutIconService;
    @Resource
    private ProductTypeService productTypeService;

    /** 获取产品信息 **/
    @Override
    public Map<String,Object> getProductInfo() {
        MarketSiteBo marketSiteBo = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        verifySiteValidity(marketSiteBo);//校验租户，站点情况
        DynamicTableNameHandler.setTenantId(marketSiteBo.getTenantId());
        List<ProductPlatform> productPlatformList = productPlatformService.getProductPlatformList(marketSiteBo.getTenantId());
        List<Product> productList = productService.getProductList(marketSiteBo.getTenantId());
        Map<String,Object> productInfoMap = new HashMap<>();
        productInfoMap.put("productPlatforms",productPlatformList);
        productInfoMap.put("products",productList);
        return productInfoMap;
    }

    @Override
    public LoadLayoutRes getLayoutInfo() {
        MarketSiteBo marketSiteBo = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        verifySiteValidity(marketSiteBo);//校验租户，站点情况
        DynamicTableNameHandler.setTenantId(marketSiteBo.getTenantId());
        LayoutConfig layoutConfig = layoutConfigService.getLayoutConfigById(marketSiteBo.getLayoutId());
        List<Media> mediaList = mediaService.getMediaList(marketSiteBo.getTenantId(),marketSiteBo.getMarketSiteId());

        List<ShortcutIcon> shortcutIconList = shortcutIconService.getShortcutIconList(marketSiteBo.getTenantId(),marketSiteBo.getMarketSiteId());
        List<ProductType> productTypeList = productTypeService.getProductTypeList(marketSiteBo.getTenantId(),marketSiteBo.getMarketSiteId());
        return new LoadLayoutRes(layoutConfig,mediaList,shortcutIconList,productTypeList);
    }
}
