package com.console.api.service.impl;

import com.console.activity.entity.Activity;
import com.console.activity.entity.ActivityReward;
import com.console.activity.entity.ActivityRule;
import com.console.activity.service.ActivityRewardService;
import com.console.activity.service.ActivityRuleService;
import com.console.activity.service.ActivityService;
import com.console.api.dto.res.VipLevelRes;
import com.console.api.service.VipAPIService;
import com.console.core.model.bo.MarketSiteBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ActivityType;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import com.console.user.entity.Vip;
import com.console.user.service.VipService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VipAPIServiceImpl extends BaseServiceImpl implements VipAPIService {
    @Resource
    private VipService vipService;
    @Resource
    private ActivityService activityService;
    @Resource
    private ActivityRuleService activityRuleService;
    @Resource
    private ActivityRewardService activityRewardService;

    @Override
    public VipLevelRes getVipLevelList() {
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        DynamicTableNameHandler.setTenantId(marketSite.getTenantId());
        RequestMember requestMember = RequestUtils.getRequestMember();
        VipLevelRes vipLevelRes = new VipLevelRes();
        List<Vip> vipList = vipService.getVipList();
        Integer userVipId = 0;
        if (requestMember != null) {
            userVipId = requestMember.getVipId();
            vipLevelRes.setOrderAmt(requestMember.getOrderAmt());
            vipLevelRes.setRechargeAmt(requestMember.getRechargeAmt());
        }

        List<VipLevelRes.VipLevel> vipLevelList = new ArrayList<>();
        for (int i = 0; i < vipList.size(); i++) {
            Vip vip = vipList.get(i);
            if ((userVipId == 0 && i == 0) || userVipId.equals(vip.getId())) {//当前等级
                vipLevelRes.setVipLevel(vip.getLevel());
                vipLevelRes.setVipName(vip.getVipName());
                vipLevelRes.setVipIcon(vip.getIcon());
            }
            VipLevelRes.VipLevel vipLevel = new VipLevelRes.VipLevel();
            vipLevel.setVipId(vip.getId());
            vipLevel.setVipLevel(vip.getLevel());
            vipLevel.setVipName(vip.getVipName());
            vipLevel.setVipIcon(vip.getIcon());
            vipLevel.setUpgradeRechargeAmt(vip.getUpgradeAmt());
            vipLevel.setUpgradeOrderAmt(vip.getUpgradeOrderAmt());
            vipLevel.setCustomerCares(vipLevel.getCustomerCares());
            vipLevelList.add(vipLevel);
        }
        vipLevelRes.setVipLeves(vipLevelList);

        List<ActivityType> vipAvtivityTypes = List.of(ActivityType.VIP_UPGRADE_REWARD,ActivityType.VIP_DAILY_SALARY,ActivityType.VIP_WEEKLY_SALARY,ActivityType.VIP_MONTHLY_SALARY);
        List<Activity> activityList = activityService.getActivityList(marketSite.getMarketSiteId());
        activityList = activityList.stream().filter(item -> vipAvtivityTypes.contains(item.getActivityType())).toList();
        Map<ActivityType, List<VipLevelRes.VipReward>> vipRewards = new HashMap<>();
        for (Activity vipActivity : activityList) {
            List<VipLevelRes.VipReward> vipRewardList = new ArrayList<>();
            List<ActivityRule> activityRules = activityRuleService.getActivityRules(vipActivity.getId());
            for (ActivityRule activityRule : activityRules) {
                VipLevelRes.VipReward vipReward = new VipLevelRes.VipReward();
                vipReward.setAcitivityId(vipActivity.getId());
                vipReward.setActivityRuleId(activityRule.getId());
                vipReward.setVipId(Integer.parseInt(activityRule.getRuleTriggerThreshold()));
                List<ActivityReward> activityRewards = activityRewardService.getActivityRewards(vipActivity.getId(),activityRule.getId());
                if (ObjectUtils.isEmpty(activityRewards)) {
                    vipReward.setRewardValue(BigDecimal.ZERO);
                } else {
                    ActivityReward activityReward = activityRewards.get(0);
                    vipReward.setRewardValue(new BigDecimal(activityReward.getRewardValue()));
                }
                vipRewardList.add(vipReward);
            }
            vipRewards.put(vipActivity.getActivityType(),vipRewardList);
        }
        vipLevelRes.setVipRewards(vipRewards);
        return vipLevelRes;
    }
}
