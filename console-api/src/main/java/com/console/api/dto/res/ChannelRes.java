package com.console.api.dto.res;

import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.dto.res.ActivityRuleDetail;
import com.console.activity.entity.ActivityReward;
import com.console.framework.constants.RewardType;
import com.console.payment.model.dto.ChannelDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChannelRes {
    private List<ChannelDTO> channels;
    private List<PaymentActivity> paymentActivities;

    public void buildPaymentChannels(List<ChannelDTO> channelList) {
        channels = channelList.stream().filter(item -> item.getPayStatus() == 1).map(item ->{
            ChannelDTO channelDTO = new ChannelDTO();
            channelDTO.setId(item.getId());
            channelDTO.setIcon(item.getIcon());
            channelDTO.setShowName(item.getShowName());
            channelDTO.setMinPayAmt(item.getMinPayAmt());
            channelDTO.setMaxPayAmt(item.getMaxPayAmt());
            channelDTO.setRealNamePayStatus(item.getRealNamePayStatus());
            channelDTO.setPayAmtArr(item.getPayAmtArr());
            return channelDTO;
        }).toList();
    }

    public void buildPaymentActivity(List<ActivityDetails> activityDetailsList) {
        if (!ObjectUtils.isEmpty(activityDetailsList)) {
            paymentActivities = new ArrayList<>();
            for (ActivityDetails activityDetails : activityDetailsList) {
                PaymentActivity paymentActivity = new PaymentActivity();
                paymentActivity.setActivityId(activityDetails.getId());
                paymentActivity.setActivityName(paymentActivity.activityName);
                List<PaymentReward> paymentRewards = new ArrayList<>();
                for (ActivityRuleDetail activityRuleDetail : activityDetails.getActivityRuleDetailList()) {
//                    private String paymentAmt;
//                    private String rewardValue;
//                    private RewardType rewardType;
                    PaymentReward paymentReward = new PaymentReward();
                    paymentReward.setPaymentAmt(activityRuleDetail.getRuleTriggerThreshold());
                    if (ObjectUtils.isEmpty(activityRuleDetail.getActivityRewards())) {
                        paymentReward.setRewardValue("0");
                        paymentReward.setRewardType(RewardType.CASH);
                    } else {
                        for (ActivityReward activityReward : activityRuleDetail.getActivityRewards()) {
                            if (activityReward.getRewardType() == RewardType.CASH || activityReward.getRewardType() == RewardType.RECHARGE_DISCOUNT) {
                                paymentReward.setRewardValue(activityReward.getRewardValue());
                                paymentReward.setRewardType(activityReward.getRewardType());
                                break;
                            }
                        }
                    }
                    paymentRewards.add(paymentReward);
                }
                paymentActivity.setPaymentRewards(paymentRewards);
                paymentActivities.add(paymentActivity);
            }
        }
    }

    public void buildCashoutChannels(List<ChannelDTO> channelList) {
        channels = channelList.stream().filter(item -> item.getCashOutStatus() == 1).map(item ->{
            ChannelDTO channelDTO = new ChannelDTO();
            channelDTO.setId(item.getId());
            channelDTO.setIcon(item.getIcon());
            channelDTO.setShowName(item.getShowName());
            channelDTO.setMinCashOutAmt(item.getMinCashOutAmt());
            channelDTO.setMaxCashOutAmt(item.getMaxCashOutAmt());
            channelDTO.setCashOutAmtArr(item.getPayAmtArr());
            return channelDTO;
        }).toList();
    }

    @Getter
    @Setter
    public static class PaymentActivity {
        private Integer activityId;
        private String  activityName;
        private List<PaymentReward> paymentRewards;
    }

    @Getter
    @Setter
    public static class PaymentReward {
        private String paymentAmt;
        private String rewardValue;
        private RewardType rewardType;
    }
}
