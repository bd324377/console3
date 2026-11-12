package com.console.activity.task.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.*;
import com.console.activity.merger.ActivityProcMerger;
import com.console.activity.service.*;
import com.console.framework.constants.ActivityType;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.time.LocalDate;
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
    protected ActivityRewardService activityRewardService;
    @Resource
    protected ReceiveRewardHandler receiveRewardHandler;
    @Resource
    protected RedisLockRegistry lockRegistry;

    public abstract ActivityType getActivityType();

    public Object triggerActivity(Integer activityId, Integer userId, Object param) {
        throw new BusinessException(400, "该活动不支持触发操作");
    }

    public Object receiveActivityAward(Integer activityId, Integer userId, Object param) {
        throw new BusinessException(400, "该活动不支持领奖操作");
    }

    public boolean verifyActivityValidity(ActivityDetails activity) {//活动有效性校验，已过活动时间或活动已关闭 true 无效 flase 有效
        return activity == null || activity.getActivityStatus() == 0
                || (LocalDateTime.now().isBefore(activity.getStartTime()) || LocalDateTime.now().isAfter(activity.getEndTime()));
    }

    public UserActivity getLastUserActivity(Integer userId,Integer activityId) {
        QueryWrapper<UserActivity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserActivity::getUserId,userId)
                .eq(UserActivity::getActivityId,activityId)
                .orderByDesc(UserActivity::getId).last("LIMIT 1");
        return userActivityService.getOne(queryWrapper);
    }

    public UserActivity verifyUserActivity(UserActivity userActivity,Integer userId,ActivityDetails activityDetails) {
        if (userActivity == null || userActivity.getExpireTime().isBefore(LocalDateTime.now())) {//上次参与活动情况已完成则继续触发新一轮活动
            Integer round = userActivity == null ? 1 : userActivity.getRound() + 1;
            userActivity = new UserActivity(userId,activityDetails.getId(),null);
            userActivity.setRound(round);
            userActivity.setExpireTime(activityDetails.getCycle() == 0 ? activityDetails.getEndTime() : LocalDate.now().atStartOfDay().plusDays(activityDetails.getCycle()));
        } else {
            if (userActivity.getStatus() == 1) return null;
        }
        return userActivity;
    }

    public ActivityDetails getActivityDetails(Integer activityId) {
        Activity activity = activityService.getActivityById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }
        return activityService.getActivityDetails(activity);
    }

    public ActivityDetails getActivityDetailsByCode(String activityCode) {
        Activity activity = activityService.getActivityByCode(activityCode);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }
        return activityService.getActivityDetails(activity);
    }
}
