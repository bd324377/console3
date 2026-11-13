package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CheckInRewardsRes {
    private Integer activityId;
    private String targetRechargeAmt;//激活所需充值金额
    private String targetOrderAmt;  //激活所需下单金额
    private Integer checkInDays = 0;//签到天数
    private Integer checkInStatus = 0;//0、未签到；1、已签到
    private List<CheckInReward> checkInRewards;//
    @Getter
    @Setter
    public static class CheckInReward {
        private BigDecimal rewardAmt;
        private String targetRechargeAmt;//激活所需充值金额
        private String targetOrderAmt;  //激活所需下单金额
    }
}
