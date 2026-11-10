package com.console.activity.task.handler.recharge;

import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.ActivityProc;
import com.console.activity.entity.UserActivity;
import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class RechargeActivityHandler extends ActivityStrategy {
    @Resource
    private RedisLockRegistry lockRegistry;

    @Override
    public ActivityType getActivityType() {
        return ActivityType.SINGLE_RECHARGE;
    }

    public Object triggerActivity(Integer activityId, Integer userId, Object param) {
        String[] params = param.toString().split(";");//0、用户ID；1、充值次数；2、充值金额
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_TRIGGER_ACTIVITY + activityId + ":" + userId);
        boolean locked = false;
        try {
            locked = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("paymentOrder.error.create.creating"));

            ActivityDetails activityDetails = getActivityDetails(activityId);
            UserActivity userActivity = getLastUserActivity(userId,activityId);
            userActivity = verifyUserActivity(userActivity,userId,activityId);
            if (userActivity.getExpireTime() == null) {
                userActivity.setExpireTime(activityDetails.getCycle() == 0 ? activityDetails.getEndTime() : LocalDate.now().atStartOfDay().plusDays(activityDetails.getCycle()));
            }
            BigDecimal orderAmt = new BigDecimal(params[2]);//订单金额

            ActivityRuleDetail triggerRule = null;//触发规则，充值活动触发最高档活动
            for (int i = 0;i < activityDetails.getActivityRuleDetailList().size();i++) {
                ActivityRuleDetail ruleDetail = activityDetails.getActivityRuleDetailList().get(i);
                if (orderAmt.compareTo(new BigDecimal(ruleDetail.getRuleTriggerThreshold())) >= 0) {
                    triggerRule = ruleDetail;
                }
            }
            if (triggerRule != null) {//成功触发
                userActivity.setTriggerTime(LocalDateTime.now());
                userActivity.setTriggeredTimes(userActivity.getTriggeredTimes() + 1);
                if (userActivity.getTriggeredTimes() >= activityDetails.getCycleRewardTimes()) {
                    userActivity.setStatus(1);
                }
                ActivityProc receiveProc = receiveRewardHandler.receiveActivityReward(userActivity,triggerRule,activityDetails.getRewardWalletType(),orderAmt);
                ActivityProc triggerProc = new ActivityProc(userActivity.getActivityId(), userActivity.getUserId(), ActivityProc.ProcType.TRIGGER_ACTIVITY);
                activityProcMerger.submit(triggerProc);
                activityProcMerger.submit(receiveProc);
                return userActivity;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
        return null;
    }
}
