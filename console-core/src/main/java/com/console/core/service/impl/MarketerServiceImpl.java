package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.entity.Marketer;
import com.console.core.mapper.MarketerMapper;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.bo.MarketerBo;
import com.console.core.service.MarketSiteService;
import com.console.core.service.MarketerService;
import com.console.framework.constants.CacheConstants;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MarketerServiceImpl extends BaseServiceImpl<MarketerMapper, Marketer> implements MarketerService {
    @Resource
    private MarketSiteService marketSiteService;

    @Override
    @Cacheable( value = CacheConstants.MARKETER, key = "T(com.console.framework.utils.RequestUtils).getRequestDomain() + ':' + #marketerId")
    public MarketerBo getMarketer(Integer marketerId) {
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        if (marketSite != null) {
            MarketerBo marketerBo = new MarketerBo(marketSite);
            QueryWrapper<Marketer> queryWrapper = new QueryWrapper<>();
            queryWrapper.select( "market_team_id", "marketer_id", "order_mode").lambda().eq(Marketer::getMarketerId,marketerId).eq(Marketer::getMarketSiteId,marketSite.getMarketSiteId());
            Marketer marketer = this.getOne(queryWrapper);
            if (marketer == null || marketer.getStatus() == Marketer.MarketerStatus.UNENABLE) {//如果停用则自动转默认人员
                queryWrapper = new QueryWrapper<>();
                queryWrapper.select("market_team_id", "marketer_id", "order_mode").lambda().eq(Marketer::getMarketerId,marketSite.getMarketerId()).eq(Marketer::getMarketSiteId,marketSite.getMarketSiteId());
                marketer = this.getOne(queryWrapper);
            }
            if (marketer != null) {
                marketerBo.buildMarketer(marketer);
                return marketerBo;
            }
        }
        return null;
    }
}
