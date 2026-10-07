package com.console.core.model.bo;

import com.console.core.entity.MarketSite;
import com.console.core.entity.MarketTeam;
import com.console.core.entity.Tenant;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class MarketSiteBo implements Serializable {//站点信息

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer tenantId;//租户ID
    private Integer marketSiteId;//站点ID
    private Integer marketTeamId;//营销团队ID(默认团队)
    private Integer marketerId;//业务员（默认业务员）ID
    private Tenant.TenantStatus tenantStatus;//租户状态
    private MarketSite.SiteStatus siteStatus;//站点状态
    private Integer tenantOrderMode;//租户默认下单模型
    private Integer siteOrderMode;//站点默认下单模型
    //分流配置
    private Integer shuntStatus;//默认分流状态
    private BigDecimal shuntRate;//默认分流率
    private Integer shuntThreshold;//默认分流阈值
    public MarketSiteBo() {}

    public MarketSiteBo(Tenant tenant, MarketSite marketSite, MarketTeam marketTeam) {
        this.setTenantId(marketSite.getTenantId());
        this.setMarketSiteId(marketSite.getId());
        this.setMarketTeamId(marketTeam.getId());
        this.setMarketerId(marketTeam.getLeaderMarketerId());
        this.setTenantStatus(tenant.getStatus());
        this.setSiteStatus(marketSite.getStatus());
        this.setTenantOrderMode(tenant.getOrderMode());
        this.setSiteOrderMode(marketSite.getOrderMode());
        this.setShuntStatus(marketTeam.getShuntStatus());
        this.setShuntRate(marketTeam.getShuntRate());
        this.setShuntThreshold(marketTeam.getShuntThreshold());
    }
}
