package com.console.user.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.console.framework.constants.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@TableName(value = "s_label",autoResultMap = true)
public class Label implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @NotNull(message = "",groups = ValidationGroup.update.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @NotBlank(groups = ValidationGroup.insert.class)
    private String labelName;//标签名称
    @NotNull(groups = ValidationGroup.insert.class)
    private Integer labelType;//标签类型
    @TableField(value = "label_rules",typeHandler = JacksonTypeHandler.class)
    @NotNull(groups = ValidationGroup.insert.class)
    private List<LabelRule> labelRules;//标签规则
    private Integer riskValue;//标签风险值
    private Integer addTime;//添加时间
    private Integer updateTime;//更新时间
    @TableField(exist = false)
    private Integer userCount;

    public void setLabelRules(Object labelRules) {
        if (labelRules != null) {
            if (labelRules instanceof String ruleStr) {
                this.labelRules = JSON.parseArray(ruleStr, LabelRule.class);
            } else {
                this.labelRules = JSON.parseArray(JSON.toJSONString(labelRules), LabelRule.class);
            }
        }
    }

    @Getter
    public enum LabelType {
        // 价值标签（充值次数，充值金额）；
        // 风险标签（同IP关联账号，同设备关联账号，充提差比率，下单爆单比率）
        RECHARGE_TIMES(0,"充值标签"),
        RECHARGE_VALUE(1,"风险标签");
        private final Integer key;
        private final String  value;

        LabelType(int key, String value) {
            this.key   = key;
            this.value = value;
        }
    }

    /**
     * 标签规则
     */
    @Getter
    @Setter
    public static class LabelRule implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Integer    ruleType;           //规则类型
        private Integer    times;              //触发次数
        private BigDecimal ruleValue;          //规则值
    }

    @Getter
    public enum RuleType {
        RECHARGE_ACC(0,"累计充值"),
        RECHARGE_SINGLE(1,"单次充值"),
        RISK_IP_LIMIT(2,"IP限制"),
        RISK_DEVICE_LIMIT(3,"设备限制");
        private final Integer key;
        private final String  value;

        RuleType(int key, String value) {
            this.key   = key;
            this.value = value;
        }
    }
}
