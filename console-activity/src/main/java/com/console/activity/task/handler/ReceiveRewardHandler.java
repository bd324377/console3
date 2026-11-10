package com.console.activity.task.handler;

import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.ActivityPrize;
import com.console.activity.entity.ActivityProc;
import com.console.activity.entity.UserActivity;
import com.console.activity.entity.UserPrize;
import com.console.activity.merger.ActivityProcMerger;
import com.console.activity.service.ActivityPrizeService;
import com.console.activity.service.UserActivityService;
import com.console.activity.service.UserPrizeService;
import com.console.core.entity.Wallet;
import com.console.core.entity.WalletProc;
import com.console.core.merger.WalletProcMerger;
import com.console.framework.constants.PrizeType;
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
    private ActivityPrizeService activityPrizeService;
    @Resource
    private UserPrizeService userPrizeService;
    @Resource
    private UserActivityService userActivityService;

    @Transactional(rollbackFor = Exception.class)
    public ActivityProc receiveActivityReward(UserActivity userActivity,
                                              ActivityRuleDetail activityRuleDetail,
                                              Wallet.WalletType walletType,
                                              BigDecimal basedOnData) {
        List<ActivityPrize> activityPrizes = new ArrayList<>();
        if (!ObjectUtils.isEmpty(activityRuleDetail.getActivityPrizes())) {
            if (activityRuleDetail.getReceiveType() == 1) {//领取全部奖励
                for (ActivityPrize activityPrize : activityRuleDetail.getActivityPrizes()) {
                    if (activityPrizeService.deductPrizeSurplusCount(activityPrize.getId())) {
                        activityPrizes.add(activityPrize);
                    }
                }
            } else {//随机领取一件
                int randomNum = SecureRandomUtil.nextInt(10000);
                int probabilitie = 0;
                for (ActivityPrize activityPrize : activityRuleDetail.getActivityPrizes()) {
                    probabilitie += activityPrize.getProbability().multiply(BigDecimal.valueOf(100)).intValue();
                    if (probabilitie >= randomNum) {
                        if (activityPrizeService.deductPrizeSurplusCount(activityPrize.getId())) {
                            activityPrizes.add(activityPrize);
                            break;
                        }
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(activityPrizes)) {
            List<WalletProc> walletProcList = new ArrayList<>();
            BigDecimal receivedAmt = new BigDecimal(0);
            for (ActivityPrize activityPrize : activityPrizes) {
                UserPrize userPrize = new UserPrize();
                userPrize.setUserId(userActivity.getUserId());
                userPrize.setPrizeIcon(activityPrize.getPrizeIcon());
                userPrize.setPrizeValue(activityPrize.getPrizeValue());
                userPrize.setAddTime(LocalDateTime.now());
                userPrize.setActivityId(activityPrize.getActivityId());
                userPrize.setWalletType(walletType);
                if (activityPrize.getValidTime() != null && activityPrize.getValidTime() > 0) {
                    userPrize.setExpireTime(LocalDateTime.now().plusDays(activityPrize.getValidTime()));
                }
                if (activityPrize.getPrizeType() == PrizeType.CASH) {
                    BigDecimal awardAmt = new BigDecimal(activityPrize.getPrizeValue());
                    userPrize.setStatus(1);
                    userPrize.setPrizeValue(activityPrize.getPrizeValue());
                    if (activityPrize.getToBeOrderRate() != null && activityPrize.getToBeOrderRate().compareTo(BigDecimal.ZERO) > 0) {
                        userPrize.setToBeOrderAmt(awardAmt.multiply(activityPrize.getToBeOrderRate()).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP));
                    }
                    WalletProc walletProc = userPrizeService.receiveAmountReward(userPrize);
                    walletProcList.add(walletProc);
                    receivedAmt = receivedAmt.add(awardAmt);
                } else if (activityPrize.getPrizeType() == PrizeType.RECHARGE_DISCOUNT) {
                    BigDecimal awardAmt = basedOnData.multiply(new BigDecimal(activityPrize.getPrizeValue())).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
                    userPrize.setPrizeValue(awardAmt.toString());
                    if (activityPrize.getToBeOrderRate() != null && activityPrize.getToBeOrderRate().compareTo(BigDecimal.ZERO) > 0) {
                        userPrize.setToBeOrderAmt(awardAmt.multiply(activityPrize.getToBeOrderRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
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
