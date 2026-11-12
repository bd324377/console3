package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_activity_rule")
public class ActivityRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;//规则ID
    private String  ruleName;//规则名称
    private Integer activityId;//活动id
    private Integer triggerType;//奖励触发类型,1、全部满足；2、满足一项
    private Integer receiveType;//奖励领取类型,1、全部领取；2、随机领取1件
    private String  ruleTriggerThreshold;//规则触发阈值,需先满足该条件，才能继续触发当前规则组的其他条件
    private Integer ruleKey;//排序
    private Integer ruleDuration;//规则时长
    private LocalDateTime addTime;//新增规则时间
    private LocalDateTime updateTime;//修改规则时间
}
