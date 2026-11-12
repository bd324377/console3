package com.console.activity.dto.req;

import com.console.activity.entity.ActivityReward;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ActivityRuleReq {

    @Getter
    @Setter
    public static class AddActivityRuleReq {
        private Integer activityId;             //
        private String  ruleName;               //规则名称
        private Integer triggerType;            //奖励触发类型,1、全部满足；2、满足一项
        private Integer receiveType;            //奖励领取类型,1、全部领取；2、随机领取1件
        private String  ruleTriggerThreshold;   //规则触发阈值,需先满足该条件，才能继续触发当前规则组的其他条件
        private Integer ruleKey;                //排序

        private List<ActivityRuleItemReq.AddActivityRuleItemReq> activityRuleItems;
        private List<ActivityReward> activityRewards;
    }
}
