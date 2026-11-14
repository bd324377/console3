package com.console.activity.task.handler.vipUpgrade;

import com.alibaba.fastjson.JSON;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.Activity;
import com.console.activity.entity.ActivityProc;
import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.entity.UserActivity;
import com.console.activity.task.handler.ActivityStrategy;
import com.console.activity.task.model.InviteNewSettlementParam;
import com.console.activity.task.model.VipUpgradeSettlementParam;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.TenantUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Getter
@Setter
public class VipUpgradeActivityHandler extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.VIP_UPGRADE_REWARD;
    }

    public Object triggerActivity(Integer activityId, Integer userId, Object param) {
        VipUpgradeSettlementParam vipUpgradeSettlementParam = JSON.parseObject(JSON.toJSONString(param),VipUpgradeSettlementParam.class);
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_TRIGGER_ACTIVITY + TenantUtils.getTenantId() + ":" + vipUpgradeSettlementParam.getUserId());
        boolean locked = false;
        try {
            locked = lock.tryLock(10000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("vipUpgradeActivity.error.create.creating"));
            List<Activity> activityList = activityService.getActivityList(vipUpgradeSettlementParam.getMarketSiteId(),ActivityType.VIP_UPGRADE_REWARD);
            if (!ObjectUtils.isEmpty(activityList)) {
                ActivityDetails activityDetails = activityService.getActivityDetails(activityList.get(0));
                activityId = activityDetails.getId();
                userId = vipUpgradeSettlementParam.getUserId();
                if (!verifyActivityValidity(activityDetails)) {
                    UserActivity userActivity = userActivityService.getLastUserActivity(userId,activityDetails.getId());
                    userActivity = userActivityService.verifyUserActivity(userActivity,userId,activityDetails);
                    if (userActivity != null) {
                        ActivityRuleDetail activityRule = activityDetails.getActivityRuleDetailList().stream().filter(item -> item.getRuleTriggerThreshold().equals(vipUpgradeSettlementParam.getVipId().toString())).findFirst().orElse(null);
                        if (activityRule == null) return null;//触发规则不存在
                        if (StringUtils.hasText(userActivity.getAchievedValue())) {
                            String[] achieveValues = userActivity.getAchievedValue().split(";");
                            if (List.of(achieveValues).contains(activityRule.getRuleTriggerThreshold())) {//已触发过，不再重复处理
                                return null;
                            }
                        }

                        userActivity.setTriggerTime(LocalDateTime.now());
                        userActivity.setTriggeredTimes(userActivity.getTriggeredTimes() + 1);
                        List<UserActivity.UnReceiveReward> unReceiveRewardList = userActivity.getUnReceiveRewards() == null ? new ArrayList<>() : userActivity.getUnReceiveRewards();
                        UserActivity.UnReceiveReward unReceiveReward = new UserActivity.UnReceiveReward();
                        unReceiveReward.setRuleId(activityRule.getId());
                        unReceiveReward.setTriggerTime(LocalDateTime.now());
                        unReceiveRewardList.add(unReceiveReward);
                        userActivity.setUnReceiveRewards(unReceiveRewardList);
                        userActivity.setRewardStatus(2);
                        if (userActivityService.saveOrUpdate(userActivity)) {
                            ActivityProc triggerProc = new ActivityProc(activityId, userId, ActivityProc.ProcType.TRIGGER_ACTIVITY);
                            activityProcMerger.submit(triggerProc);
                        }
                        return userActivity;
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
        return null;
    }
}
