package com.console.product.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id; // 产品id
    @JSONField(name = "platId")
    private Integer platformId; // 产商id
    @JSONField(name = "categoryId")
    private ProductCategory category; // 产品分类
    @JSONField(name = "gameCode")
    private String productCode; // 产品代码
    private String officialProductCode; // 官方产品代码
    @JSONField(name = "gameName")
    private String productName; // 产品名称（json）
    private Integer status; // 产品状态：1、正常；0、关闭
    private LocalDateTime addTime; // 添加时间
    private Integer sort; // 排序值

    @Getter
    public enum ProductCategory {
        VIDEO_SLOTS(1, "电子"),
        FISHING(2, "捕鱼"),
        LIVE_GAMES(3, "现场"),
        STREET(4, "街机"),
        ELE_LOTTERY(5, "电子彩票"),
        CHESS(6, "棋牌");

        @JsonValue
        @EnumValue
        private final int code;
        private final String desc;

        ProductCategory(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @JsonCreator
        public static ProductCategory fromCode(int code) {
            for (ProductCategory modeEnum : ProductCategory.values()) {
                if (modeEnum.code == code) {
                    return modeEnum;
                }
            }
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }
}
