package com.console.api.service.impl;

import com.console.activity.service.ActivityService;
import com.console.api.service.ActivityAPIService;
import com.console.core.model.bo.MarketSiteBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ActivityAPIServiceImpl extends BaseServiceImpl implements ActivityAPIService {
    @Resource
    private ActivityService activityService;

    @Override
    public Object getActivityList() {
        MarketSiteBo marketSiteBo = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        if (marketSiteBo == null) return null;
        DynamicTableNameHandler.setTenantId(marketSiteBo.getTenantId());
        return activityService.getActivityList(marketSiteBo.getMarketSiteId());
    }
}
