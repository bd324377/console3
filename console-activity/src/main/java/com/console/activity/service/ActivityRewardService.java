package com.console.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.activity.entity.ActivityReward;

import java.util.List;

public interface ActivityRewardService extends IService<ActivityReward> {
    boolean deductRewardSurplusCount(Integer ActivityPrizeId);
    List<ActivityReward> getActivityRewards(Integer activityId, Integer activityRuleId);
}
