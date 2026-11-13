package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.*;
import com.console.activity.merger.ActivityProcMerger;
import com.console.activity.service.ActivityService;
import com.console.activity.service.UserActivityService;
import com.console.activity.task.disruptor.ActivityDisruptorManager;
import com.console.activity.task.handler.ReceiveRewardHandler;
import com.console.api.dto.req.ReceiveInviteRewardReq;
import com.console.api.dto.res.CheckInRewardsRes;
import com.console.api.dto.res.InviteNewUserRewardsRes;
import com.console.api.service.ActivityAPIService;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.bo.MarketerBo;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ActivityEvent;
import com.console.framework.constants.ActivityType;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.Result;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class ActivityAPIServiceImpl extends BaseServiceImpl implements ActivityAPIService {
    @Resource
    private ActivityService activityService;
    @Resource
    private UserActivityService userActivityService;
    @Resource
    private ActivityDisruptorManager activityDisruptorManager;
    @Resource
    private RedisLockRegistry lockRegistry;
    @Resource
    private ReceiveRewardHandler receiveRewardHandler;
    @Resource
    protected ActivityProcMerger activityProcMerger;

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
                userActivity = userActivityService.getLastUserActivity(requestMember.getId(), activityDetails.getId());
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

    @Override
    public Object receiveInviteReward(ReceiveInviteRewardReq receiveInviteRewardReq) {
        RequestMember requestMember = new RequestMember();
        if (requestMember.getInviteRewardStatus() == 0) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("activityAPI.receive.error.noInvitationPermissions"));
        }
        String lockKey = "ACQUIRE_LOCK:RECEIVE_AGENT_AWARD" + requestMember.getTenantId() + ":" + receiveInviteRewardReq.getActivityId() + ":" + requestMember.getId();
        Lock lock = lockRegistry.obtain(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("inviteRewardActivity.error.create.creating"));
            UserActivity userActivity = userActivityService.getLastUserActivity(requestMember.getId(), receiveInviteRewardReq.getActivityId());
            if (userActivity == null || ObjectUtils.isEmpty(userActivity.getUnReceiveRewards())) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("activityAPI.receive.error.expire"));
            }
            Activity activity = activityService.getActivityById(receiveInviteRewardReq.getActivityId());
            if (activity == null || activity.getActivityStatus() == 0) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("activityAPI.receive.error.expire"));
            }
            ActivityDetails activityDetails = activityService.getActivityDetails(activity);
            List<UserActivity.UnReceiveReward> unReceiveRewardList = new ArrayList<>();
            if (receiveInviteRewardReq.getActivityRuleId() == null) {//批量领取
                unReceiveRewardList.addAll(userActivity.getUnReceiveRewards());
            } else {//指定领取
                UserActivity.UnReceiveReward unReceiveTrigger = userActivity.getUnReceiveRewards().stream()
                        .filter(item -> item.getRuleId().equals(receiveInviteRewardReq.getActivityRuleId())).findFirst().orElse(null);
                if (unReceiveTrigger == null) {
                    return Result.failed(500,I18nUtil.getI8nMsg("activityAPI.receive.error.expire"));
                }
                unReceiveRewardList.add(unReceiveTrigger);
            }
            List<ActivityProc> activityProcList = new ArrayList<>();
            BigDecimal receiveAmt = new BigDecimal(0);
            for (UserActivity.UnReceiveReward unReceiveReward : unReceiveRewardList) {
                ActivityRuleDetail activityRuleDetail = activityDetails.getActivityRuleDetailList()
                        .stream().filter(item -> item.getId().equals(unReceiveReward.getRuleId())).findFirst().orElse(null);
                if (activityRuleDetail == null) {
                    return Result.failed(500,I18nUtil.getI8nMsg("activityAPI.receive.error.expire"));
                }
                ActivityProc receiveProc = receiveRewardHandler.receiveActivityReward(userActivity,activityRuleDetail,activityDetails.getRewardWalletType(),null);
                activityProcList.add(receiveProc);
                receiveAmt = receiveAmt.add(receiveProc.getReceiveAmt());
            }
            for (ActivityProc activityProc : activityProcList) {
                activityProcMerger.submit(activityProc);
            }
            return receiveAmt;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
    }

    @Override
    public CheckInRewardsRes checkIntroduction() {
        MarketerBo marketerBo = getMarketerBo();
        DynamicTableNameHandler.setTenantId(marketerBo.getTenantId());
        List<Activity> activityList = activityService.getActivityList(marketerBo.getMarketSiteId(),ActivityType.SIGN_IN);
        if (!ObjectUtils.isEmpty(activityList)) {
            Activity activity = activityList.get(0);
            ActivityDetails activityDetails = activityService.getActivityDetails(activity);
            CheckInRewardsRes checkInRewardsRes = new CheckInRewardsRes();
            RequestMember requestMember = RequestUtils.getRequestMember();
            int checkInIndex = 0,checkInStatus = 0;
            if (requestMember != null) {
                UserActivity userActivity = userActivityService.getLastUserActivity(requestMember.getId(), activity.getId());
                userActivity = userActivityService.verifyUserActivity(userActivity,requestMember.getId(),activityDetails);
                checkInIndex = userActivity.getTriggeredTimes();
                if (userActivity.getTriggerTime() != null) {
                    if (DateUtils.isToday(userActivity.getTriggerTime())) checkInStatus = 1;
                }
            }
            checkInRewardsRes.setCheckInDays(checkInStatus == 0 ? checkInIndex + 1 : checkInIndex);
            checkInRewardsRes.setCheckInStatus(checkInStatus);
            List<CheckInRewardsRes.CheckInReward> checkInRewards = new ArrayList<>();
            List<ActivityRuleType> rechargeRuleList = List.of(ActivityRuleType.CUMULATIVE_RECHARGE_AMT,ActivityRuleType.CYCLE_CUMULATIVE_RECHARGE_AMT,ActivityRuleType.TOTAL_CUMULATIVE_RECHARGE_AMT);
            List<ActivityRuleType> orderRuleList = List.of(ActivityRuleType.CUMULATIVE_ORDER_AMT,ActivityRuleType.CYCLE_CUMULATIVE_ORDER_AMT,ActivityRuleType.TOTAL_CUMULATIVE_ORDER_AMT);
            for (int i = 0;i < activityDetails.getActivityRuleDetailList().size();i++) {
                ActivityRuleDetail activityRuleDetail = activityDetails.getActivityRuleDetailList().get(i);
                CheckInRewardsRes.CheckInReward checkInReward = new CheckInRewardsRes.CheckInReward();
                for (ActivityRuleItem activityRuleItem : activityRuleDetail.getActivityRuleItems()) {
                    if (rechargeRuleList.contains(activityRuleItem.getRuleType())) {
                        checkInReward.setTargetRechargeAmt(activityRuleItem.getRuleValue());
                    } else if (orderRuleList.contains(activityRuleItem.getRuleType())) {
                        checkInReward.setTargetOrderAmt(activityRuleItem.getRuleValue());
                    }
                }
                if (i == checkInIndex) {
                    checkInRewardsRes.setTargetRechargeAmt(checkInReward.getTargetRechargeAmt());
                    checkInRewardsRes.setTargetOrderAmt(checkInReward.getTargetOrderAmt());
                }
                checkInRewards.add(checkInReward);
            }
            checkInRewardsRes.setCheckInRewards(checkInRewards);
            return checkInRewardsRes;
        }
        return null;
    }

    @Override
    public Object checkIn() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        String redisKey = "ACTIVITY:CHECK_IN:" + requestMember.getTenantId() + ":" + requestMember.getId();
        Lock lock = lockRegistry.obtain(redisKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("checkInActivity.error.create.creating"));
            List<Activity> activityList = activityService.getActivityList(requestMember.getSiteId(),ActivityType.SIGN_IN);
            Integer activityId;
            if (!ObjectUtils.isEmpty(activityList)) {
                activityId = activityList.get(0).getId();
            } else {
                throw new BusinessException(500,"签到活动不存在");
            }
            return receiveRewards(activityId,ActivityType.SIGN_IN, requestMember.getId(), requestMember.getTenantId(),null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
    }

    @Override
    public CompletableFuture<Object> redeeming(String redeemCode) {
        if (!StringUtils.hasText(redeemCode)) {
            CompletableFuture<Object> resultFuture = new CompletableFuture<>();
            resultFuture.complete(I18nUtil.getI8nMsg("activityAPI.receive.error.redemptionCode.isEmpty"));
            return resultFuture.thenApply(result -> Result.failed(500,result.toString()));
        }
        RequestMember requestMember = RequestUtils.getRequestMember();
        Activity activity = activityService.getActivityByCode(redeemCode);
        String redisKey = "ACTIVITY:REDEEM:" + requestMember.getTenantId() + ":" + activity.getId() + ":" + requestMember.getId();
        Lock lock = lockRegistry.obtain(redisKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(5000, TimeUnit.MILLISECONDS);
            if (!locked) throw new BusinessException(500, I18nUtil.getI8nMsg("checkInActivity.error.create.creating"));
            return receiveRewards(activity.getId(), ActivityType.RED_ENVELOPE, requestMember.getId(), requestMember.getTenantId(),null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) lock.unlock();
        }
    }



    public CompletableFuture<Object> receiveRewards(Integer activityId,ActivityType activityType,Integer userId,Integer tenantId,Object param) {
        ActivityEvent activityEvent = new ActivityEvent(activityId,activityType,userId,tenantId,param);
        CompletableFuture<Object> resultFuture = activityDisruptorManager.publishActivityEvent(activityEvent);
        return resultFuture.thenApply(result -> {
            if (result instanceof ActivityProc receiveProc) {
                return Result.success(receiveProc.getReceiveAmt());
            } else {
                if (result instanceof String error) {
                    return Result.failed(500, error);
                }
            }
            return Result.failed(500);
        });
    }
}
