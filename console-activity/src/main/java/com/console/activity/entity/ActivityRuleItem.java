package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_activity_rule_item")
public class ActivityRuleItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;//规则明细ID
    private Integer activityId;//活动id
    private Integer activityRuleId;//规则id
    private Integer ruleType;//规则类型
    private String ruleValue;//规则值
}
