package com.console.api.service.impl;

import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.Activity;
import com.console.activity.entity.ActivityReward;
import com.console.activity.entity.ActivityRule;
import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.service.ActivityService;
import com.console.api.dto.res.ChannelRes;
import com.console.api.service.ActivityAPIService;
import com.console.api.service.ChannelAPIService;
import com.console.core.entity.Tenant;
import com.console.core.model.bo.MarketSiteBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ActivityType;
import com.console.framework.utils.RequestUtils;
import com.console.payment.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChannelAPIServiceImpl extends BaseServiceImpl implements ChannelAPIService {
    @Resource
    private ChannelService channelService;
    @Resource
    private ActivityService activityService;

    @Override
    public ChannelRes getPaymentChannels() {
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
        ChannelRes paymentChannelres = new ChannelRes();
        paymentChannelres.buildPaymentChannels(channelService.getChannelList());
        List<Activity> activityList = activityService.getActivityList(marketSite.getMarketSiteId(), ActivityType.SINGLE_RECHARGE);
        List<ActivityDetails> activityDetailsList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(activityList)) {
            for (Activity activity : activityList) {
                activityDetailsList.add(activityService.getActivityDetails(activity));
            }
            paymentChannelres.buildPaymentActivity(activityDetailsList);
        }
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
