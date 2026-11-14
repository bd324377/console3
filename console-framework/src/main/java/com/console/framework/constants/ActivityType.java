package com.console.framework.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum ActivityType {
    UNDEFINED(0, "未定义"),
    SIGN_IN(1, "普通签到"),
    RED_ENVELOPE(2, "兑换码"),
    FIRST_RECHARGE(3, "首充"),
    SINGLE_RECHARGE(4, "单次充值"),
    ACCUMULATE_RECHARGE(5, "累计充值"),
    INVITE_NEW_USERS(6, "邀新"),
    PLACE_ORDER(7,"下单"),
    RANKING_BOARD(8, "排行榜"),
    PACKAGE_CARD(9, "周/月卡"),
    RECHARGE_RAKE_BACK(10, "充值返佣"),
    ORDER_RAKE_BACK(11,"订单返佣"),
    VIP_CASH_BACK(11, "vip返现活动"),
    VIP_UPGRADE_REWARD(12,"VIP升级奖励"),
    VIP_DAILY_SALARY(13,"VIP日薪"),
    VIP_WEEKLY_SALARY(14,"VIP周薪"),
    VIP_MONTHLY_SALARY(15,"VIP月薪"),
    VIP_WEEK_LOSE_BACK(16,"VIP每周输返"),
    VIP_WEEK_SALARY(17,"VIP周薪"),
    WEEK_RECHARGE(18,"周累计充值"),
    VIP_CHECK_IN(19,"vip签到"),
    PIN_DUO_DUO(13, "拼多多砍一刀抽奖机会"),
    LOTTERY(14, "抽奖");

    @JsonValue
    @EnumValue
    private final Integer key;
    private final String value;

    ActivityType(int key, String value) {
        this.key   = key;
        this.value = value;
    }

    public static ActivityType getActivityTypeByKey(Integer key) {
        for (ActivityType activityType : ActivityType.values()) {
            if (Objects.equals(activityType.getKey(), key)) {
                return activityType;
            }
        }
        return ActivityType.UNDEFINED;
    }
}
