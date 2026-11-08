package com.console.activity.dto.req;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.activity.entity.Activity;
import com.console.activity.entity.ActivityRule;
import com.console.activity.entity.ActivityRuleItem;
import com.console.core.entity.AdminProc;
import com.console.core.entity.Wallet;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.constants.LoginChannel;
import com.console.framework.domain.IUpdateField;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityReq {
    @Getter
    @Setter
    public static class AddActivityReq {
        private String  activityName;//活动名称
        private String  blurb;//活动简介
        private String  activityCode;//活动编码
        private String  exclusionCode;//互斥编码,互斥编码不为空且同一编码活动互斥，只有完成一个任务后才能参与另一活动
        private String  icon;//活动图片
        private Integer cycle;//活动周期
        private Integer cycleRewardTimes;//周期内可触发次数
        private LoginChannel loginChannel;//登录渠道
        private Wallet.WalletType rewardWalletType;//奖励进入的钱包类型，同钱包类型
        private Activity.ActivityType type;//活动类型
        private LocalDateTime startTime;//开始时间
        private LocalDateTime endTime;//结束时间
        private Integer status;//活动状态：0、停用；1、可用
        private Integer sort;//排序，值越高排越前
        private String  routePath;//路由地址
        private Integer isHot;//是否为热门活动
        private String  remarks;//备注

        private List<ActivityRuleReq.AddActivityRuleReq> activityRules;

        public Activity buildActivity() {
            Activity activity = new Activity();
            BeanUtils.copyProperties(this,activity);
            return activity;
        }

        public List<ActivityRule> buildActivityRules(Integer activityId) {
            if (!ObjectUtils.isEmpty(activityRules)) {
                List<ActivityRule> ruleList = new ArrayList<>();
                for (ActivityRuleReq.AddActivityRuleReq activityRuleReq : activityRules) {
                    ActivityRule activityRule = new ActivityRule();
                    BeanUtils.copyProperties(activityRuleReq,activityRule);
                    activityRule.setActivityId(activityId);
                    activityRule.setAddTime(LocalDateTime.now());
                    activityRule.setUpdateTime(LocalDateTime.now());
                    ruleList.add(activityRule);
                }
                return ruleList;
            }
            return null;
        }

        public List<ActivityRuleItem> buildActivityRuleItems(List<ActivityRule> ruleList) {
            if (!ObjectUtils.isEmpty(activityRules)) {
                List<ActivityRuleItem> ruleItemList = new ArrayList<>();
                for (int i = 0; i < activityRules.size();i++) {
                    ActivityRule activityRule = ruleList.get(i);
                    ActivityRuleReq.AddActivityRuleReq activityRuleReq = activityRules.get(i);
                    if (!ObjectUtils.isEmpty(activityRuleReq.getActivityRuleItems())) {
                        for (ActivityRuleItemReq.AddActivityRuleItemReq addActivityRuleItemReq : activityRuleReq.getActivityRuleItems()) {
                            ActivityRuleItem ruleItem = new ActivityRuleItem();
                            BeanUtils.copyProperties(addActivityRuleItemReq, ruleItem);
                            ruleItem.setActivityId(activityRule.getActivityId());
                            ruleItem.setActivityRuleId(activityRule.getId());
                            ruleItemList.add(ruleItem);
                        }
                    }
                }
                return ruleItemList;
            }
            return null;
        }
    }

    @Getter
    @Setter
    public static class UpdateActivityReq extends BaseUpdateReq<Activity> {
        @NotNull
        private Integer id;
        @NotNull
        private Integer marketSiteId;
        private String activityName;//活动名称
        private String blurb;//活动简介
        private String activityCode;//活动编码
        private String exclusionCode;//互斥编码,互斥编码不为空且同一编码活动互斥，只有完成一个任务后才能参与另一活动
        private String icon;//活动图片
        private Integer cycle;//活动周期
        private Integer cycleRewardTimes;//周期内可触发次数
        private LoginChannel loginChannel;//登录渠道
        private Wallet.WalletType rewardWalletType;//奖励进入的钱包类型，同钱包类型
        private LocalDateTime startTime;//开始时间
        private LocalDateTime endTime;//结束时间
        private Integer status;//活动状态：0、停用；1、可用
        private Integer sort;//排序，值越高排越前
        private String routePath;//路由地址
        private Integer isHot;//是否为热门活动
        private String remarks;//备注

        @Getter
        public enum UpdateField implements IUpdateField<Activity> {
            ACTIVITY_NAME("activityName",Activity::getActivityName,"activity.proc.update.activityName"),
            BLURB("blurb",Activity::getBlurb,"activity.proc.update.blurb"),
            ACTIVITY_CODE("activityCode",Activity::getActivityCode,"activity.proc.update.activityCode"),
            EXCLUSION_CODE("exclusionCode",Activity::getExclusionCode,"activity.proc.update.exclusionCode"),
            ICON("icon",Activity::getIcon,"activity.proc.update.icon"),
            CYCLE("cycle",Activity::getCycle,"activity.proc.update.cycle"),
            CYCLE_REWARD_TIMES("cycleRewardTimes",Activity::getCycleRewardTimes,"activity.proc.update.cycleRewardTimes"),
            LOGIN_CHANNEL("loginChannel",Activity::getLoginChannel,"activity.proc.update.loginChannel"),
            REWARD_WALLET_TYPE("rewardWalletType",Activity::getRewardWalletType,"activity.proc.update.rewardWalletType"),
            STATUS("status",Activity::getActivityStatus,"activity.proc.update.status"),
            SORT("sort",Activity::getSort,"activity.proc.update.sort"),
            ROUTE_PATH("routePath",Activity::getRoutePath,"activity.proc.update.routePath"),
            IS_HOT("isHot",Activity::getIsHot,"activity.proc.update.isHot"),
            REMARKS("remarks",Activity::getRemarks,"activity.proc.update.remarks");

            private final String fieldName;
            private final SFunction<Activity, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<Activity, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }

        @Override
        protected SFunction<Activity, ?> idFunction() {
            return Activity::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.ACTIVITY;
        }

        @Override
        protected IUpdateField<Activity> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }
}
