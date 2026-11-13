package com.console.activity.task.handler.redeem;

import com.console.activity.dto.bo.UserRechargeAndOrderAmt;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.*;
import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.TenantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class RedeemActivityStrategy extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.RED_ENVELOPE;
    }

    public Object triggerActivity(Integer activityId, Integer userId, Object param) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_TRIGGER_ACTIVITY + TenantUtils.getTenantId() + ":" + activityId + ":" + userId);
        boolean locked = false;
        try {
            locked = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("redeemActivity.error.create.creating"));

            ActivityDetails activityDetails = getActivityDetails(activityId);
            if (verifyActivityValidity(activityDetails)) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("活动已过期或失效，请联系管理员"));
            }
            if (activityDetails.getActivityType() != getActivityType()) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("非兑换码活动，不做处理"));
            }
            UserActivity userActivity = userActivityService.getLastUserActivity(userId, activityId);
            userActivity = userActivityService.verifyUserActivity(userActivity,userId,activityDetails);
            if (userActivity == null) throw new BusinessException(500, I18nUtil.getI8nMsg("已使用过该兑换码，请勿重复兑换"));

            if (!ObjectUtils.isEmpty(activityDetails.getActivityRuleDetailList())) {
                ActivityRuleDetail activityRuleDetail = activityDetails.getActivityRuleDetailList().get(0);
                if (!ObjectUtils.isEmpty(activityRuleDetail.getActivityRuleItems())) {
                    String errorStr = triggerRuleItem(activityRuleDetail);
                    if (StringUtils.hasText(errorStr)) {
                        throw new BusinessException(500,errorStr);
                    }
                }
                //成功触发，获得奖励
                userActivity.setTriggeredTimes(userActivity.getTriggeredTimes() + 1);
                userActivity.setTriggerTime(LocalDateTime.now());
                userActivity.setStatus(1);
                ActivityProc receiveProc = receiveRewardHandler.receiveActivityReward(userActivity,activityRuleDetail,activityDetails.getRewardWalletType(),null);
                ActivityProc triggerProc = new ActivityProc(userActivity.getActivityId(), userActivity.getUserId(), ActivityProc.ProcType.TRIGGER_ACTIVITY);
                activityProcMerger.submit(triggerProc);
                activityProcMerger.submit(receiveProc);
                return receiveProc;
            }
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
    }
}
