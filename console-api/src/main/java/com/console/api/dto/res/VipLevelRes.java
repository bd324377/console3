package com.console.api.dto.res;

import com.console.framework.constants.ActivityType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class VipLevelRes implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //当前等级信息
    private Integer vipId;
    private String  vipName;
    private Integer vipLevel;
    private String  vipIcon;                //VIP图标
    private BigDecimal orderAmt;            //当前下单金额
    private BigDecimal rechargeAmt;         //当前充值金额
    private Integer    rewardStatus = 0;    //奖励状态 0、未进行；1、进行中；2、已达成条件未领奖；3、已领奖
    private List<VipLevel> vipLeves;    //VIP等级列表
    private Map<ActivityType, List<VipReward>> vipRewards;//VIP相关奖励 升级、日周月奖励

    @Getter
    @Setter
    public static class VipLevel {
        private Integer vipId;
        private String  vipName;
        private Integer vipLevel;
        private String  vipIcon;                //VIP图标
        private String  customerCares;//专属客服地址
        private BigDecimal upgradeRechargeAmt;//升级所需充值金额
        private BigDecimal upgradeOrderAmt;//升级所需下单金额
    }

    @Getter
    @Setter
    public static class VipReward {
        private Integer    vipId;
        private Integer    acitivityId;
        private Integer    activityRuleId;
        private BigDecimal rewardConfig;
        private BigDecimal rewardValue;
        private Integer    rewardStatus = 0;//奖励状态 0、未进行；1、进行中；2、已达成条件未领奖；3、已领奖
    }
}
