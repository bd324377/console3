package com.console.api.service.impl;

import com.console.core.entity.MarketSite;
import com.console.core.entity.Tenant;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.bo.MarketerBo;
import com.console.core.service.MarketSiteService;
import com.console.core.service.ParameterService;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import jakarta.annotation.Resource;

public abstract class BaseServiceImpl {
    @Resource
    protected MarketSiteService marketSiteService;
    @Resource
    protected ParameterService parameterService;

    protected void verifySiteValidity(MarketSiteBo marketSite) {
        if (marketSite == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("reg.error.domain.notExist"));
        }
        if (marketSite.getSiteStatus() == MarketSite.SiteStatus.CLOSED) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("reg.error.site.notOpen"));
        }
        if (marketSite.getTenantStatus() == Tenant.TenantStatus.CLOSED) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("reg.error.tenant.notOpen"));
        }
    }
}
