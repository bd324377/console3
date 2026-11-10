package com.console.activity.dto.res;

import com.console.framework.constants.ActivityType;

import com.console.activity.entity.Activity;
import com.console.core.entity.Wallet;
import com.console.framework.constants.LoginChannel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ActivityDetails {
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

    private List<ActivityRuleDetail> activityRuleDetailList;
}
