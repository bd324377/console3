package com.console.activity.task.handler.redeem;

import com.console.activity.dto.bo.UserRechargeAndOrderAmt;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.ActivityRule;
import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.entity.ActivityRuleType;
import com.console.activity.entity.UserActivity;
import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.TenantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

            ActivityDetails activityDetails = getActivityDetailsByCode(param.toString());
            if (verifyActivityValidity(activityDetails)) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("活动已过期或失效，请联系管理员"));
            }
            if (activityDetails.getActivityType() != getActivityType()) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("非兑换码活动，不做处理"));
            }
            UserActivity userActivity = getLastUserActivity(userId, activityId);
            userActivity = verifyUserActivity(userActivity,userId,activityDetails);
            if (userActivity == null) throw new BusinessException(500, I18nUtil.getI8nMsg("已使用过该兑换码，请勿重复兑换"));

            UserRechargeAndOrderAmt userRechargeAndOrderAmt = null;
            if (!ObjectUtils.isEmpty(activityDetails.getActivityRuleDetailList())) {//不为空
                ActivityRuleDetail activityRuleDetail = activityDetails.getActivityRuleDetailList().get(0);
                if (!ObjectUtils.isEmpty(activityRuleDetail.getActivityRuleItems())) {
                    ActivityRuleItem activityRuleItem = activityRuleDetail.getActivityRuleItems().get(0);

                    for (ActivityRuleItem ruleItem : activityRuleDetail.getActivityRuleItems()) {
//                        if (activityRuleItem.getRuleType() == )
                    }
                }
            }

            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
    }
}
