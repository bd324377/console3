package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.RewardType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@TableName("s_activity_reward")
public class ActivityReward implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;//活动奖励ID
    private Integer activityId;//活动id
    private Integer activityRuleId;//规则id
    private Integer activityRuleItemId;//规则组中的规则明细ID（只用于多重活动嵌套时使用）
    private String rewardIcon;//奖励图标
    private RewardType rewardType;//奖品类型
    private String rewardValue;//奖励值
    private BigDecimal rewardMaxValue;//奖励最大金额值
    private BigDecimal toBeOrderRate;//奖励待下单率
    private Integer count;//奖品总数，对外展示，0表示无穷
    private Integer validTime;//领取后有效时间 0表永久有效
    private Integer surpCount;//剩余奖品数量
    private BigDecimal probability;//中奖概率
}
