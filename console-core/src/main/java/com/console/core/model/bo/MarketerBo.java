package com.console.core.model.bo;

import com.console.core.entity.MarketSite;
import com.console.core.entity.Marketer;
import com.console.core.entity.Tenant;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class MarketerBo implements Serializable {//业务员信息

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer tenantId;//租户ID
    private Integer siteId;//站点ID
    private Integer marketTeamId;//营销团队ID
    private Integer marketerId;//业务员（管理员）ID
    private Tenant.TenantStatus tenantStatus;//租户状态
    private MarketSite.SiteStatus siteStatus;//站点状态
    private Marketer.MarketerStatus marketerStatus = Marketer.MarketerStatus.ENABLE;//业务员状态
    private Integer tenantOrderMode;//租户下单模型
    private Integer siteOrderMode;//站点下单模型
    private Integer marketerOrderMode;//业务员下单模型
    //分流配置
    private Integer shuntStatus;//分流状态
    private BigDecimal shuntRate;//分流率
    private Integer shuntThreshold;//分流阈值

    public MarketerBo() {}

    public MarketerBo(MarketSiteBo marketSiteBo) {
        BeanUtils.copyProperties(marketSiteBo,this);
    }

    public void buildMarketer(Marketer marketer) {
        this.setMarketTeamId(marketer.getMarketTeamId());
        this.setMarketerId(marketer.getMarketerId());
        this.setMarketerStatus(marketer.getStatus());
        this.setMarketerOrderMode(marketer.getOrderMode());
    }

    public static void verifyValidity(MarketerBo marketer) {
        if (marketer == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("reg.error.domain.notExist"));
        }
        if (marketer.getTenantStatus() == Tenant.TenantStatus.CLOSED) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("reg.error.tenant.notOpen"));
        }
        if (marketer.getSiteStatus() == MarketSite.SiteStatus.CLOSED) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("reg.error.site.notOpen"));
        }
    }
}
