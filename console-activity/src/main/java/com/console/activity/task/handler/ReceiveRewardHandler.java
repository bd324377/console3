package com.console.activity.task.handler;

import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.ActivityReward;
import com.console.activity.entity.ActivityProc;
import com.console.activity.entity.UserActivity;
import com.console.activity.entity.UserPrize;
import com.console.activity.service.ActivityRewardService;
import com.console.activity.service.UserActivityService;
import com.console.activity.service.UserPrizeService;
import com.console.core.entity.Wallet;
import com.console.core.entity.WalletProc;
import com.console.core.merger.WalletProcMerger;
import com.console.framework.constants.RewardType;
import com.console.framework.utils.SecureRandomUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReceiveRewardHandler {
    @Resource
    private WalletProcMerger walletProcMerger;
    @Resource
    private ActivityRewardService activityRewardService;
    @Resource
    private UserPrizeService userPrizeService;
    @Resource
    private UserActivityService userActivityService;

    @Transactional(rollbackFor = Exception.class)
    public ActivityProc receiveActivityReward(UserActivity userActivity,
                                              ActivityRuleDetail activityRuleDetail,
                                              Wallet.WalletType walletType,
                                              BigDecimal basedOnData) {
        List<ActivityReward> activityRewards = new ArrayList<>();
        if (!ObjectUtils.isEmpty(activityRuleDetail.getActivityRewards())) {
            if (activityRuleDetail.getReceiveType() == 1) {//领取全部奖励
                for (ActivityReward activityReward : activityRuleDetail.getActivityRewards()) {
                    if (activityRewardService.deductRewardSurplusCount(activityReward.getId())) {
                        activityRewards.add(activityReward);
                    }
                }
            } else {//随机领取一件
                int randomNum = SecureRandomUtil.nextInt(10000);
                int probabilitie = 0;
                for (ActivityReward activityReward : activityRuleDetail.getActivityRewards()) {
                    probabilitie += activityReward.getProbability().multiply(BigDecimal.valueOf(100)).intValue();
                    if (probabilitie >= randomNum) {
                        if (activityRewardService.deductRewardSurplusCount(activityReward.getId())) {
                            activityRewards.add(activityReward);
                            break;
                        }
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(activityRewards)) {
            List<WalletProc> walletProcList = new ArrayList<>();
            BigDecimal receivedAmt = new BigDecimal(0);
            for (ActivityReward activityReward : activityRewards) {
                UserPrize userPrize = new UserPrize();
                userPrize.setUserId(userActivity.getUserId());
                userPrize.setPrizeIcon(activityReward.getRewardIcon());
                userPrize.setPrizeValue(activityReward.getRewardValue());
                userPrize.setAddTime(LocalDateTime.now());
                userPrize.setActivityId(activityReward.getActivityId());
                userPrize.setWalletType(walletType);
                if (activityReward.getValidTime() != null && activityReward.getValidTime() > 0) {
                    userPrize.setExpireTime(LocalDateTime.now().plusDays(activityReward.getValidTime()));
                }
                if (activityReward.getRewardType() == RewardType.CASH) {
                    BigDecimal awardAmt = new BigDecimal(activityReward.getRewardValue());
                    userPrize.setStatus(1);
                    userPrize.setPrizeValue(activityReward.getRewardValue());
                    if (activityReward.getToBeOrderRate() != null && activityReward.getToBeOrderRate().compareTo(BigDecimal.ZERO) > 0) {
                        userPrize.setToBeOrderAmt(awardAmt.multiply(activityReward.getToBeOrderRate()).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP));
                    }
                    WalletProc walletProc = userPrizeService.receiveAmountReward(userPrize);
                    walletProcList.add(walletProc);
                    receivedAmt = receivedAmt.add(awardAmt);
                } else if (activityReward.getRewardType() == RewardType.RECHARGE_DISCOUNT) {
                    BigDecimal awardAmt = basedOnData.multiply(new BigDecimal(activityReward.getRewardValue())).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
                    userPrize.setPrizeValue(awardAmt.toString());
                    if (activityReward.getToBeOrderRate() != null && activityReward.getToBeOrderRate().compareTo(BigDecimal.ZERO) > 0) {
                        userPrize.setToBeOrderAmt(awardAmt.multiply(activityReward.getToBeOrderRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                    }
                    userPrize.setStatus(1);
                    WalletProc walletProc = userPrizeService.receiveAmountReward(userPrize);
                    walletProcList.add(walletProc);
                    receivedAmt = receivedAmt.add(awardAmt);
                }
            }
            if (receivedAmt.compareTo(BigDecimal.ZERO) > 0) {
                if (userActivity.getReceivedAmt() == null) {
                    userActivity.setReceivedAmt(receivedAmt);
                } else {
                    userActivity.setReceivedAmt(userActivity.getReceivedAmt().add(receivedAmt));
                }
            }
            if (userActivityService.saveOrUpdate(userActivity)) {
                for (WalletProc walletProc : walletProcList) {
                    walletProcMerger.submit(walletProc);
                }
                ActivityProc activityProc = new ActivityProc(userActivity.getActivityId(), userActivity.getUserId(),
                        ActivityProc.ProcType.RECEIVE_REWARD);
                activityProc.setReceiveAmt(receivedAmt);
                return activityProc;
            }
        }
        return null;
    }
}
