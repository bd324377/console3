package com.console.activity.dto.req;

import lombok.Getter;
import lombok.Setter;

public class ActivityRuleItemReq {
    @Getter
    @Setter
    public static class AddActivityRuleItemReq {
        private Integer ruleType;//规则类型
        private String ruleValue;//规则值
    }
}
