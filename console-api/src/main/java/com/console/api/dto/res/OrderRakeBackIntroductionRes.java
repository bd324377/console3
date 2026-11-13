package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderRakeBackIntroductionRes {
    private List<OrderRakeBackConfig> subRakeBackRules;//直属下级下单返佣规则
    private List<OrderRakeBackConfig> otherRakeBackRules;//其他层级下单返佣规则

    @Getter
    @Setter
    public static class OrderRakeBackConfig {
        private Integer level;
        private BigDecimal ruleValue;
        private BigDecimal rewardValue;
    }
}
