package com.console.framework.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ProductBelong {
    UNFIND(0,"未知类型"),
    SLOTS(1,"电子"),
    FISHING(2,"捕鱼"),
    LIVE(3,"现场"),
    STREET(4,"街机"),
    ELE_LOTTERY(5,"电子彩票"),
    CHESS(6, "棋牌");

    @JsonValue
    @EnumValue
    private final int code;
    private final String desc;

    ProductBelong(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ProductBelong fromCode(int code) {
        for (ProductBelong productBelong : ProductBelong.values()) {
            if (productBelong.code == code) {
                return productBelong;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
