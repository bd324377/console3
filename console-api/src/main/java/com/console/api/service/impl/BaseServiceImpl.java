package com.console.api.service.impl;

import com.console.core.entity.MarketSite;
import com.console.core.entity.Tenant;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.bo.MarketerBo;
import com.console.core.service.MarketSiteService;
import com.console.core.service.MarketerService;
import com.console.core.service.ParameterService;
import com.console.core.service.TenantService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.EncryptUtils;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.user.model.bo.InviteNewConfig;
import com.console.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;

public abstract class BaseServiceImpl {
    @Resource
    protected MarketerService marketerService;
    @Resource
    protected MarketSiteService marketSiteService;
    @Resource
    protected ParameterService parameterService;
    @Resource
    private UserService userService;

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

    protected MarketerBo getMarketerBo() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        if (requestMember != null && requestMember.getUserType() == 0) {
            return marketerService.getMarketer(requestMember.getMarketerId());
        } else {
            MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
            DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
            int inviteType = 2;Integer inviteCode = null;    //邀请类型 默认渠道推广 1、代理；2、站点业务员
            String inviteCodeStr = RequestUtils.getHttpHeader("inviteCode");
            if (StringUtils.hasText(inviteCodeStr)) {//有带邀请码
                String inviteCodeData = EncryptUtils.decodeFromBase64Str(inviteCodeStr);
                String[] inviteCodeDatas = inviteCodeData.split("_");
                inviteType = inviteCodeDatas.length >= 2 ? Integer.parseInt(inviteCodeDatas[0]) : inviteType;
                inviteCode = inviteCodeDatas.length >= 2 ? Integer.valueOf(inviteCodeDatas[1]) : inviteCode;

                if (inviteCode != null) {
                    MarketerBo marketer;
                    if (inviteType == 2) {//业务员邀新
                        marketer = marketerService.getMarketer(inviteCode);
                    } else {//代理邀新
                        InviteNewConfig inviteNewConfig = userService.getUserInviteNewConfig(inviteCode);
                        marketer = marketerService.getMarketer(inviteNewConfig.getMarketerId());
                    }
                    return marketer;
                }
            }
            return marketerService.getMarketer(marketSite.getMarketerId());
        }
    }
}
