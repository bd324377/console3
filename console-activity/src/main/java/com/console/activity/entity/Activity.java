package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.core.entity.Wallet;
import com.console.framework.constants.LoginChannel;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 活动表
 * </p>
 *
 * @author aha
 * @since 2024-09-24
 */
@Getter
@Setter
@TableName(value = "s_activity",autoResultMap = true)
public class Activity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer marketSiteId;//站点ID
    private String activityName;//活动名称
    private String blurb;//活动简介
    private String activityCode;//活动编码
    private String exclusionCode;//互斥编码,互斥编码不为空且同一编码活动互斥，只有完成一个任务后才能参与另一活动
    private String icon;//活动图片
    private Integer cycle;//活动周期
    private Integer cycleRewardTimes;//周期内可触发次数
    private LoginChannel loginChannel;//登录渠道
    private Wallet.WalletType rewardWalletType;//奖励进入的钱包类型，同钱包类型
    private ActivityType activityType;//活动类型
    private LocalDateTime startTime;//开始时间
    private LocalDateTime endTime;//结束时间
    private Integer activityStatus;//活动状态：0、停用；1、可用
    private Integer showStatus;//展示状态 1、展示；0、不展示
    private Integer sort;//排序，值越高排越前
    private String routePath;//路由地址
    private Integer isHot;//是否为热门活动
    private String remarks;//备注

    @Getter
    public enum ActivityType {
        UNDEFINED(0, "未定义"),
        SIGN_IN(1, "签到"),
        RED_ENVELOPE(2, "兑换码"),
        FIRST_RECHARGE(3, "首充"),
        SINGLE_RECHARGE(4, "单次充值"),
        ACCUMULATE_RECHARGE(5, "累计充值"),
        FREE_ORDERING(6, "邀新"),
        PLACE_ORDER(7,"下单"),
        RANKING_BOARD(8, "排行榜"),
        PACKAGE_CARD(9, "周/月卡"),
        RAKE_BACK(10, "返佣"),
        CASH_BACK(11, "返水"),
        UPGRADE_REWARD(12,"升级"),
        COLLECT_CARDS(13, "收藏卡牌"),
        LOTTERY(14, "抽奖");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        ActivityType(int key, String value) {
            this.key   = key;
            this.value = value;
        }

        public static ActivityType getActivityTypeByKey(Integer key) {
            for (ActivityType activityType : ActivityType.values()) {
                if (Objects.equals(activityType.getKey(), key)) {
                    return activityType;
                }
            }
            return ActivityType.UNDEFINED;
        }
    }

}