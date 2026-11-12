package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.Activity;
import com.console.activity.entity.UserActivity;
import com.console.activity.service.ActivityService;
import com.console.activity.service.UserActivityService;
import com.console.api.dto.res.InviteNewUserRewardsRes;
import com.console.api.service.ActivityAPIService;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.bo.MarketerBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ActivityType;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ActivityAPIServiceImpl extends BaseServiceImpl implements ActivityAPIService {
    @Resource
    private ActivityService activityService;
    @Resource
    private UserActivityService userActivityService;

    @Override
    public Object getActivityList() {
        MarketSiteBo marketSiteBo = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        if (marketSiteBo == null) return null;
        DynamicTableNameHandler.setTenantId(marketSiteBo.getTenantId());
        return activityService.getActivityList(marketSiteBo.getMarketSiteId());
    }

    @Override
    public InviteNewUserRewardsRes getInviteNewUserRewards() {
        MarketerBo marketerBo = getMarketerBo();
        if (marketerBo == null) return null;
        DynamicTableNameHandler.setTenantId(marketerBo.getTenantId());
        List<Activity> activityList = activityService.getActivityList(marketerBo.getMarketSiteId());
        Activity inviteNewUserActivity = activityList.stream().filter(item -> item.getActivityType() == ActivityType.INVITE_NEW_USERS).findFirst().orElse(null);
        if (inviteNewUserActivity != null && inviteNewUserActivity.getActivityStatus() == 1 && inviteNewUserActivity.getStartTime().isBefore(LocalDateTime.now()) && inviteNewUserActivity.getEndTime().isAfter(LocalDateTime.now())) {
            InviteNewUserRewardsRes inviteNewUserRewardsRes = new InviteNewUserRewardsRes();
            ActivityDetails activityDetails = activityService.getActivityDetails(inviteNewUserActivity);
            inviteNewUserRewardsRes.setActivityId(activityDetails.getId());
            inviteNewUserRewardsRes.setActivationRechargeAmt(marketerBo.getActivationRechargeAmt());
            inviteNewUserRewardsRes.setActivationOrderAmt(marketerBo.getActivationOrderAmt());
            RequestMember requestMember = RequestUtils.getRequestMember();
            UserActivity userActivity = null;
            if (requestMember != null) {
                userActivity = getLastUserActivity(requestMember.getId(), activityDetails.getId());
                if (userActivity != null && StringUtils.hasText(userActivity.getAchievedValue())) {
                    inviteNewUserRewardsRes.setInviteCount(Integer.parseInt(userActivity.getAchievedValue()));
                }
            }
            List<InviteNewUserRewardsRes.InviteNewUserReward> inviteNewUserRewards = new ArrayList<>();
            for (int i = 0; i < activityDetails.getActivityRuleDetailList().size();i++) {
                ActivityRuleDetail activityRuleDetail = activityDetails.getActivityRuleDetailList().get(i);
                InviteNewUserRewardsRes.InviteNewUserReward inviteNewUserReward = new InviteNewUserRewardsRes.InviteNewUserReward();
                inviteNewUserReward.setRuleId(activityRuleDetail.getId());
                inviteNewUserReward.setTargetCount(activityRuleDetail.getRuleTriggerThreshold());
                if (ObjectUtils.isEmpty(activityRuleDetail.getActivityRewards())) {
                    inviteNewUserReward.setRewardValue("0");
                } else {
                    inviteNewUserReward.setRewardValue(activityRuleDetail.getActivityRewards().get(0).getRewardValue());
                }
                if (userActivity != null) {
                    if (i == userActivity.getTriggeredTimes()) {
                        if (activityDetails.getActivityRuleDetailList().size() > i) {
                            inviteNewUserReward.setRewardStatus(1);
                            inviteNewUserRewardsRes.setTargetCount(Integer.parseInt(activityRuleDetail.getRuleTriggerThreshold()));
                        }
                    } else if (i > userActivity.getTriggeredTimes()) {
                        inviteNewUserReward.setRewardStatus(0);
                    } else {
                        if (ObjectUtils.isEmpty(userActivity.getUnReceiveRewards()) ||
                                userActivity.getUnReceiveRewards().stream().filter(item -> Objects.equals(item.getRuleId(), activityRuleDetail.getId())).findFirst().orElse(null) == null) {
                            inviteNewUserReward.setRewardStatus(3);
                        } else {
                            inviteNewUserReward.setRewardStatus(2);
                        }
                    }
                }
                inviteNewUserRewards.add(inviteNewUserReward);
            }
            inviteNewUserRewardsRes.setInviteNewUserRewards(inviteNewUserRewards);
            return inviteNewUserRewardsRes;
        }
        return null;
    }

    public UserActivity getLastUserActivity(Integer userId,Integer activityId) {
        QueryWrapper<UserActivity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserActivity::getUserId,userId)
                .eq(UserActivity::getActivityId,activityId)
                .orderByDesc(UserActivity::getId).last("LIMIT 1");
        return userActivityService.getOne(queryWrapper);
    }
}
