package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.utils.TenantUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("p_activity_proc")
public class ActivityProc implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer activityId;//活动id
    private Integer userId;//用户id
    private LocalDate procDate;//触发日期
    private LocalDateTime procTime;//触发时间
    private ProcType procType;//过程类型；1、参与活动/接取任务；2、触发奖励；3、领取奖励
    private BigDecimal receiveAmt;//活动奖励领取金额

    @TableField(exist = false)
    private Integer procCount;
    @TableField(exist = false)
    private Integer tenantId;
    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;

    public ActivityProc() {}

    public ActivityProc(Integer activityId, Integer userId,ProcType procType) {
        this.activityId = activityId;
        this.userId   = userId;
        this.procTime = LocalDateTime.now();
        this.procDate = LocalDate.now();
        this.procType = procType;
        this.tenantId = TenantUtils.getTenantId();
    }

    @Getter
    public enum ProcType {
        //0、参与活动/接取任务；1、触发奖励；2、领取奖励；3、奖励过期
        ACCEPT_ACTIVITY(0, "参与活动/接取任务"),
        TRIGGER_ACTIVITY(1, "触发奖励(达成活动触发条件)"),
        RECEIVE_REWARD(2, "领取奖励"),
        REWARD_EXPIRE(3, "奖励过期");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        public static ProcType getProcTypeByKey(int key) {
            return switch (key) {
                case 1 -> TRIGGER_ACTIVITY;
                case 2 -> RECEIVE_REWARD;
                case 3 -> REWARD_EXPIRE;
                default -> ACCEPT_ACTIVITY;
            };
        }

        ProcType(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
