package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class InviteNewUserRewardsRes {
    private Integer activityId;
    private BigDecimal activationRechargeAmt;//激活所需充值金额
    private BigDecimal activationOrderAmt;  //激活所需下单金额
    private Integer inviteCount = 0;//邀新人数
    private Integer targetCount = 1;//触发下一阶段奖励目前人数
    private List<InviteNewUserReward> inviteNewUserRewards;

    @Getter
    @Setter
    public static class InviteNewUserReward {
        private Integer ruleId;//规则ID
        private String targetCount;//目标数量
        private String rewardValue;//奖励金额
        private Integer rewardStatus = 0;
    }
}
