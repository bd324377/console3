package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@TableName(value = "r_user_activity", autoResultMap = true)
public class UserActivity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;//用户id
    private String  deviceId;//设备唯一标识
    private Integer activityId;//活动id
    private Integer round;//轮数，阶段（周期任务每完成一个周期+1）
    private LocalDateTime acceptTime;//活动接取时间/首次触发时间（周期任务作为基准线时间）
    private Integer triggeredTimes;//任务已触发次数
    private LocalDateTime triggerTime;//上一次触发奖励时间
    private LocalDateTime expireTime;//此次触发周期到期时间
    private Integer triggerKey;//已触发最大规则组key
    private Integer status;//任务状态 0、未完结；1、已完结（结束一个任务周期）
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<UnReceiveReward> unReceiveRewards;//未领取奖励的触发次数，多次未领奖以逗号隔开
    private Integer rewardStatus;//领奖状态：1、没有待领取奖励；2、存在待领取奖励
    @TableField(typeHandler = JacksonTypeHandler.class)
//    private List<ActivityRuleItem> completeRuleItems;//完成的规则情况
    private String achievedValue;//已触发值
    private BigDecimal receivedAmt;//已领取金额

    /**
     * 已触发规则情况
     */
    @Getter
    @Setter
    public static class UnReceiveReward implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private Integer ruleId;         //奖励对应的规则id
        private LocalDateTime triggerTime;    //触发奖励时间
        private LocalDateTime expireTime;     //奖励领取有效时间
    }

    public UserActivity(Integer userId, Integer activityId, String deviceId) {
        this.userId = userId;
        this.activityId = activityId;
        this.acceptTime = LocalDateTime.now();
        this.triggeredTimes = 0;
        this.triggerKey = 0;
        this.deviceId = deviceId;
//        this.completeRuleItems = new ArrayList<>();
        this.receivedAmt = new BigDecimal("0");
//        this.expireTime = activity.getCycle() == 0 ? activity.getEndTime() : LocalDateTime.now().plusDays(activity.getCycle());
    }
}
