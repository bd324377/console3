package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ActivityRuleType {
    SINGLE_RECHARGE_AMT(1,"单次充值金额","activity.rule.singleRechargeAmt"),
    CUMULATIVE_RECHARGE_AMT(2,"累计充值金额",""),
    CYCLE_CUMULATIVE_RECHARGE_AMT(3,"周期内累计充值金额",""),
    TOTAL_CUMULATIVE_RECHARGE_AMT(4,"总累计充值金额",""),
    CUMULATIVE_ORDER_AMT(5,"累计下单金额",""),
    CYCLE_CUMULATIVE_ORDER_AMT(6,"周期内累计下单金额",""),
    TOTAL_CUMULATIVE_ORDER_AMT(7,"总累计下单金额",""),
    CUMULATIVE_ORDER_LOSE_AMT(8,"累计下单亏损金额",""),
    CYCLE_CUMULATIVE_ORDER_LOSE_AMT(9,"周期内累计下单亏损金额",""),
    TOTAL_CUMULATIVE_ORDER_LOSE_AMT(10,"总累计下单亏损金额",""),
    TOTAL_VALID_INVITEE_COUNT(11,"总累计有效邀新人数","Número cumulativo de convites válidos"),
    DAILY_VALID_INVITEE_COUNT(12,"当日有效邀请人数","Número de convites válidos no dia"),
    CYCLE_VALID_INVITEE_COUNT(13,"周期内有效邀请人数","Número de convites válidos dentro do ciclo"),
    DAILY_INVITEE_COUNT(14,"当日邀请人数","Número cumulativo de convites");
//
//    CUMULATIVE_RECHARGE_AMT(2,"指定时间内累计充值金额",""),
//    YESTERDAY_CUMULATIVE_RECHARGE_AMT(3,"昨日日累计充值金额",""),
//    CYCLE_CUMULATIVE_RECHARGE_AMT(4,"周期内累计充值金额",""),
//    TOTAL_CUMULATIVE_RECHARGE_AMT(5,"总累计充值金额",""),
//    CUMULATIVE_ORDER_AMT(6,"指定时间内累计下单金额",""),
//    YESTERDAY_CUMULATIVE_ORDER_AMT(7,"昨日日累计下单金额",""),
//    CYCLE_CUMULATIVE_ORDER_AMT(7,"周期内累计下单金额",""),
//    TOTAL_CUMULATIVE_ORDER_AMT(8,"总累计下单金额",""),
//    CUMULATIVE_ORDER_LOSE_AMT(9,"指定时间内累计亏损金额",""),
//    YESTERDAY_CUMULATIVE_ORDER_LOSE_AMT(10,"昨日日累计亏损金额",""),
//    CYCLE_CUMULATIVE_ORDER_LOSE_AMT(11,"周期内累计亏损金额",""),
//    TOTAL_CUMULATIVE_ORDER_LOSE_AMT(12,"总累计亏损金额","");
    @JsonValue
    @EnumValue
    private final Integer key;
    private final String  value;
    private final String  langKey;//语言包键值
    ActivityRuleType(int key, String value, String langKey) {
        this.key   = key;
        this.value = value;
        this.langKey = langKey;
    }
}
