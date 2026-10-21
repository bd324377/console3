package com.console.product.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.console.framework.constants.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@TableName("s_order_mode")
public class OrderMode implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @NotNull(groups = ValidationGroup.update.class)
    private Integer id;//固定Id 1、默认模式；2、白名单；3、黑名单（前三种固定只有一个），自定义可以多个
    @NotNull(groups = ValidationGroup.insert.class)
    private Integer modeType;//模式类型 1、默认模式；2、白名单；3、黑名单；4、试玩模式；5、测试模式；6、自定义；7、杀模式
    @NotNull(groups = ValidationGroup.insert.class)
    private String  modeName;//模式名称
    private Integer marketSiteId;//站点ID
    @TableField(typeHandler = JacksonTypeHandler.class)
    @NotNull(groups = ValidationGroup.insert.class)
    private List<MerchantSwitchRule> switchRules;//模式对应的厂商商户列表，最少一个


    public void setSwitchRules(Object object) {
        if (object instanceof String platformsString) {
            switchRules = JSON.parseArray(platformsString, MerchantSwitchRule.class);
        } else {
            switchRules = JSON.parseArray(JSON.toJSONString(object), MerchantSwitchRule.class);
        }
    }

    @Getter
    @Setter
    public static class MerchantSwitchRule {
        private Integer switchRule;//切换规则（下单次数）
        private Integer platformBelongRuleId;//产商归属ID
    }
//    private String
@Getter
public enum OrderModeType {
    UNKNOWN(0,"未知"),
    DEFAULT(1,"默认"),
    WHITELIST(2,"白名单"),
    BLACKLIST(3,"黑名单"),
    TRIAL(4,"试玩模式"),
    TEST(5,"测试模式"),
    CUSTOM(6,"自定义");

    @JsonValue
    @EnumValue
    private final Integer key;
    private final String value;

    OrderModeType(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
}
