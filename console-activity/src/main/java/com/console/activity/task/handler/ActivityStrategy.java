package com.console.activity.task.handler;

import com.console.activity.dto.bo.UserRechargeAndOrderAmt;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.*;
import com.console.activity.mapper.ActivityTriggerMapper;
import com.console.activity.merger.ActivityProcMerger;
import com.console.activity.service.*;
import com.console.framework.constants.ActivityType;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import jakarta.annotation.Resource;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class ActivityStrategy {
    @Resource
    protected ActivityProcMerger activityProcMerger;
    @Resource
    protected ActivityService activityService;
    @Resource
    protected UserActivityService userActivityService;
    @Resource
    protected ActivityTriggerMapper activityTriggerMapper;
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

    public String triggerRuleItem(ActivityRuleDetail activityRuleDetail) {
        LocalDate startDate = LocalDate.now(),endDate = LocalDate.now();
        if (activityRuleDetail.getRuleDuration() != null && activityRuleDetail.getRuleDuration() > 0) {
            startDate = startDate.minusDays(activityRuleDetail.getRuleDuration());
        }
        UserRechargeAndOrderAmt userRechargeAndOrderAmt = activityTriggerMapper.selectTotalRechargeAndOrderAmt(startDate,endDate);
        if (userRechargeAndOrderAmt == null) userRechargeAndOrderAmt = new UserRechargeAndOrderAmt();
        for (ActivityRuleItem activityRuleItem : activityRuleDetail.getActivityRuleItems()) {
            if (activityRuleItem.getRuleType() == ActivityRuleType.CUMULATIVE_RECHARGE_AMT) {
                if (userRechargeAndOrderAmt.getRechargeAmt().compareTo(new BigDecimal(activityRuleItem.getRuleValue())) < 0) {
                    return I18nUtil.getI8nMsg("未达成充值条件");
                }
            }
            if (activityRuleItem.getRuleType() == ActivityRuleType.CUMULATIVE_ORDER_AMT) {
                if (userRechargeAndOrderAmt.getOrderAmt().compareTo(new BigDecimal(activityRuleItem.getRuleValue())) < 0) {
                    return I18nUtil.getI8nMsg("未达成充值条件");
                }
            }
            if (activityRuleItem.getRuleType() == ActivityRuleType.CUMULATIVE_ORDER_LOSE_AMT) {
                if (userRechargeAndOrderAmt.getOrderAmt().compareTo(new BigDecimal(activityRuleItem.getRuleValue())) < 0) {
                    return I18nUtil.getI8nMsg("未达成充值条件");
                }
            }
        }
        return null;
    }
}
