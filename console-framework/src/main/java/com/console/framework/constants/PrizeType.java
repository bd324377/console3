package com.console.framework.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PrizeType {
    UNDEFINED(0, "未定义奖励"),
    ACTUAL(1, "实物奖励"),
    CASH(2, "现金奖励"),
    RECHARGE_DISCOUNT(3, "充值奖励"),
    ORDER_DISCOUNT(4, "下单奖励"),
    FREE_LOTTERY(5, "免费抽奖次数"),
    COLLECTION_CARD(6, "集卡卡片"),
    PACKAGE_CARD(7,"套餐卡"),
    PROVIDENT_FUND_RATE(8,"公积金");

    @JsonValue
    @EnumValue
    private final Integer key;
    private final String value;

    PrizeType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static PrizeType getPrizeByKey(int key) {
        return switch (key) {
            case 1 -> ACTUAL;
            case 2 -> CASH;
            case 3 -> RECHARGE_DISCOUNT;
            case 4 -> ORDER_DISCOUNT;
            case 5 -> FREE_LOTTERY;
            case 6 -> COLLECTION_CARD;
            default -> UNDEFINED;
        };
    }
}
