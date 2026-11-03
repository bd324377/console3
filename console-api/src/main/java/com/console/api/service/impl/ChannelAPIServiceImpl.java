package com.console.api.service.impl;

import com.console.api.dto.res.ChannelRes;
import com.console.api.service.ChannelAPIService;
import com.console.core.entity.Tenant;
import com.console.core.model.bo.MarketSiteBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.utils.RequestUtils;
import com.console.payment.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ChannelAPIServiceImpl extends BaseServiceImpl implements ChannelAPIService {
    @Resource
    private ChannelService channelService;

    @Override
    public ChannelRes getPaymentChannels() {
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
        ChannelRes paymentChannelres = new ChannelRes();
        paymentChannelres.buildPaymentChannels(channelService.getChannelList());
        return paymentChannelres;
    }

    @Override
    public ChannelRes getCashOutChannels() {
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
        ChannelRes cashOutChannelres = new ChannelRes();
        cashOutChannelres.buildCashoutChannels(channelService.getChannelList());
        return cashOutChannelres;
    }
}
