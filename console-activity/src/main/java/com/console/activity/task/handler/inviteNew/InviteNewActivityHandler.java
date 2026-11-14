package com.console.activity.task.handler.inviteNew;

import com.alibaba.fastjson.JSON;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.*;
import com.console.activity.merger.ActivityProcMerger;
import com.console.activity.task.handler.ActivityStrategy;
import com.console.activity.task.model.InviteNewSettlementParam;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.TenantUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class InviteNewActivityHandler extends ActivityStrategy {
    @Resource
    private ActivityProcMerger activityProcMerger;
    @Override
    public ActivityType getActivityType() {
        return ActivityType.INVITE_NEW_USERS;
    }

    public Object triggerActivity(Integer activityId, Integer userId, Object param) {
        InviteNewSettlementParam inviteNewSettlementParam = JSON.parseObject(JSON.toJSONString(param),InviteNewSettlementParam.class);
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_TRIGGER_ACTIVITY + TenantUtils.getTenantId() + ":" + inviteNewSettlementParam.getParentId());
        boolean locked = false;
        try {
            locked = lock.tryLock(10000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("inviteNewActivity.error.create.creating"));
            List<Activity> activityList = activityService.getActivityList(inviteNewSettlementParam.getMarketSiteId(),ActivityType.INVITE_NEW_USERS);
            if (!ObjectUtils.isEmpty(activityList)) {
                ActivityDetails activityDetails = activityService.getActivityDetails(activityList.get(0));
                activityId = activityDetails.getId();
                userId = inviteNewSettlementParam.getParentId();
                if (!verifyActivityValidity(activityDetails)) {
                    UserActivity userActivity = userActivityService.getLastUserActivity(inviteNewSettlementParam.getParentId(), activityDetails.getId());
                    userActivity = userActivityService.verifyUserActivity(userActivity,inviteNewSettlementParam.getParentId(), activityDetails);
                    if (userActivity != null) {
                        List<ActivityRuleDetail>  activityRuleList = new ArrayList<>();
                        for (int i = userActivity.getTriggeredTimes(); i < activityDetails.getActivityRuleDetailList().size(); i++) {
                            ActivityRuleDetail activityRule = activityDetails.getActivityRuleDetailList().get(i);
                            ActivityRuleItem ruleItem = activityRule.getActivityRuleItems().get(0);
                            if (inviteNewSettlementParam.getSubValidUserCount() >= Integer.parseInt(ruleItem.getRuleValue())) {//成功触发规则
                                activityRuleList.add(activityRule);
                            } else {
                                userActivity.setAchievedValue(inviteNewSettlementParam.getSubValidUserCount().toString());
                                break;
                            }
                            if (i == activityDetails.getActivityRuleDetailList().size() - 1) {
                                userActivity.setAchievedValue(inviteNewSettlementParam.getSubValidUserCount().toString());
                            }
                        }

                        userActivity.setTriggerTime(LocalDateTime.now());
                        userActivity.setTriggeredTimes(userActivity.getTriggeredTimes() + activityRuleList.size());
                        List<UserActivity.UnReceiveReward> unReceiveRewardList = userActivity.getUnReceiveRewards() == null ? new ArrayList<>() : userActivity.getUnReceiveRewards();
                        List<ActivityProc> activityProcList = new ArrayList<>();//触发过程列表

                        for (ActivityRuleDetail activityRule : activityRuleList) {
                            UserActivity.UnReceiveReward unReceiveReward = new UserActivity.UnReceiveReward();
                            unReceiveReward.setRuleId(activityRule.getId());
                            unReceiveReward.setTriggerTime(LocalDateTime.now());
                            unReceiveRewardList.add(unReceiveReward);
                            ActivityProc activityProc = new ActivityProc(activityId, userId, ActivityProc.ProcType.TRIGGER_ACTIVITY);
                            activityProcList.add(activityProc);
                        }

                        userActivity.setUnReceiveRewards(unReceiveRewardList);
                        userActivity.setRewardStatus(2);
                        if (userActivityService.saveOrUpdate(userActivity)) {
                            activityProcList.forEach(activityProc -> activityProcMerger.submit(activityProc));
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
