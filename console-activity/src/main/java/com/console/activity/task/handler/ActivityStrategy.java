package com.console.activity.task.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.*;
import com.console.activity.merger.ActivityProcMerger;
import com.console.activity.service.*;
import com.console.framework.constants.ActivityType;
import com.console.framework.domain.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class ActivityStrategy {
    @Resource
    protected ActivityProcMerger activityProcMerger;
    @Resource
    protected ActivityService activityService;
    @Resource
    protected UserActivityService userActivityService;
    @Resource
    protected ActivityRuleService activityRuleService;
    @Resource
    protected ActivityRuleItemService activityRuleItemService;
    @Resource
    protected ActivityPrizeService activityPrizeService;
    @Resource
    protected ReceiveRewardHandler receiveRewardHandler;

    public abstract ActivityType getActivityType();

    public Object triggerActivity(Integer activityId, Integer userId, Object param) {
        throw new BusinessException(400, "该活动不支持触发操作");
    }

    public Object receiveActivityAward(Integer activityId, Integer userId, Object param) {
        throw new BusinessException(400, "该活动不支持领奖操作");
    }

    public UserActivity getLastUserActivity(Integer userId,Integer activityId) {
        QueryWrapper<UserActivity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserActivity::getUserId,userId)
                .eq(UserActivity::getActivityId,activityId)
                .orderByDesc(UserActivity::getId).last("LIMIT 1");
        return userActivityService.getOne(queryWrapper);
    }

    public UserActivity verifyUserActivity(UserActivity userActivity,Integer userId,Integer activityId) {
        if (userActivity == null || userActivity.getExpireTime().isBefore(LocalDateTime.now())) {//上次参与活动情况已完成则继续触发新一轮活动
            Integer round = userActivity == null ? 1 : userActivity.getRound() + 1;
            userActivity = new UserActivity(userId,activityId,null);
            userActivity.setRound(round);
        } else {
            if (userActivity.getStatus() == 1) return null;
        }
        return userActivity;
    }

    public ActivityDetails getActivityDetails(Integer activityId) {
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }
        ActivityDetails activityDetails = new ActivityDetails();
        BeanUtils.copyProperties(activity,activityDetails);
        List<ActivityRuleDetail> activityRuleDetailList = new ArrayList<>();
        List<ActivityRule> activityRules = activityRuleService.getActivityRules(activityId);
        for (ActivityRule activityRule : activityRules) {
            ActivityRuleDetail activityRuleDetail = new ActivityRuleDetail();
            BeanUtils.copyProperties(activityRule,activityRuleDetail);
            List<ActivityRuleItem> activityRuleItems = activityRuleItemService.getActivityRuleItems(activityId,activityRule.getId());
            List<ActivityPrize> activityPrizes = activityPrizeService.getActivityPrizes(activityId,activityRule.getId());
            activityRuleDetail.setActivityRuleItems(activityRuleItems);
            activityRuleDetail.setActivityPrizes(activityPrizes);
            activityRuleDetailList.add(activityRuleDetail);
        }
        activityDetails.setActivityRuleDetailList(activityRuleDetailList);
        return activityDetails;
    }


}
