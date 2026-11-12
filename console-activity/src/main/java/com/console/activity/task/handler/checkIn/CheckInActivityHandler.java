package com.console.activity.task.handler.checkIn;

import com.console.activity.dto.bo.UserRechargeAndOrderAmt;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.ActivityProc;
import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.entity.ActivityRuleType;
import com.console.activity.entity.UserActivity;
import com.console.activity.mapper.ActivityTriggerMapper;
import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class CheckInActivityHandler extends ActivityStrategy {
    @Resource
    private ActivityTriggerMapper activityTriggerMapper;
    @Override
    public ActivityType getActivityType() {
        return ActivityType.SIGN_IN;
    }

    public Object triggerActivity(Integer activityId, Integer userId, Object param) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_TRIGGER_ACTIVITY + TenantUtils.getTenantId() + ":" + activityId + ":" + userId);
        boolean locked = false;
        try {
            locked = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("checkInActivity.error.create.creating"));
            String deviceId = param.toString();
            ActivityDetails activityDetails = getActivityDetails(activityId);
            if (activityDetails.getActivityType() != getActivityType()) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("非签到活动，不做处理"));
            }
            UserActivity userActivity = getLastUserActivity(userId,activityId);
            userActivity = verifyUserActivity(userActivity,userId,activityDetails);
            if (userActivity == null) throw new BusinessException(500,"已获取当前周期所有签到奖励，请明天继续");
            int checkInIndex = 0;
            if (userActivity.getTriggerTime() != null) {
                if (DateUtils.isToday(userActivity.getTriggerTime()))
                    throw new BusinessException(500, "今日已领取签到奖励，请明天继续");
                if (DateUtils.isBeforeYesterday(userActivity.getTriggerTime())) {//已断签，重新开始
                    userActivity = new UserActivity(userId,activityId,deviceId);
                    userActivity.setExpireTime(activityDetails.getCycle() == 0 ? activityDetails.getEndTime() : LocalDate.now().atStartOfDay().plusDays(activityDetails.getCycle()));
                } else {
                    checkInIndex = userActivity.getTriggeredTimes();
                }
            }
            if (activityDetails.getActivityRuleDetailList().size() < checkInIndex + 1) throw new BusinessException(500,"已获取当前周期所有签到奖励，请明天继续");
            ActivityRuleDetail activityRuleDetail = activityDetails.getActivityRuleDetailList().get(checkInIndex);
            if (!ObjectUtils.isEmpty(activityRuleDetail.getActivityRuleItems())) {
                UserRechargeAndOrderAmt userRechargeAndOrderAmt = activityTriggerMapper.selectTotalRechargeAndOrderAmt(LocalDate.now(),LocalDate.now());
                if (userRechargeAndOrderAmt == null) userRechargeAndOrderAmt = new UserRechargeAndOrderAmt();
                for (ActivityRuleItem activityRuleItem : activityRuleDetail.getActivityRuleItems()) {
                    if (activityRuleItem.getRuleType() == ActivityRuleType.CUMULATIVE_RECHARGE_AMT) {
                        if (userRechargeAndOrderAmt.getRechargeAmt().compareTo(new BigDecimal(activityRuleItem.getRuleValue())) < 0) {
                            throw new BusinessException(500,"未达成充值条件");
                        }
                    }
                    if (activityRuleItem.getRuleType() == ActivityRuleType.CUMULATIVE_ORDER_AMT) {
                        if (userRechargeAndOrderAmt.getOrderAmt().compareTo(new BigDecimal(activityRuleItem.getRuleValue())) < 0) {
                            throw new BusinessException(500,"未达成下单条件");
                        }
                    }
                }
            }
            userActivity.setTriggeredTimes(userActivity.getTriggeredTimes() + 1);
            userActivity.setTriggerTime(LocalDateTime.now());
            if (userActivity.getTriggeredTimes() >= activityDetails.getCycleRewardTimes()) {
                userActivity.setStatus(1);
            }
            ActivityProc receiveProc = receiveRewardHandler.receiveActivityReward(userActivity,activityRuleDetail,activityDetails.getRewardWalletType(),null);
            ActivityProc triggerProc = new ActivityProc(userActivity.getActivityId(), userActivity.getUserId(), ActivityProc.ProcType.TRIGGER_ACTIVITY);
            activityProcMerger.submit(triggerProc);
            activityProcMerger.submit(receiveProc);
            return receiveProc;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
    }
}
