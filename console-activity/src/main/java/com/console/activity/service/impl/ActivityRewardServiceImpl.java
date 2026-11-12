package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.activity.entity.ActivityReward;
import com.console.activity.mapper.ActivityRewardMapper;
import com.console.activity.service.ActivityRewardService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityRewardServiceImpl extends BaseServiceImpl<ActivityRewardMapper, ActivityReward> implements ActivityRewardService {

    @Override
    public boolean deductRewardSurplusCount(Integer activityPrizeId) {
        UpdateWrapper<ActivityReward> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(ActivityReward::getId,activityPrizeId).gt(ActivityReward::getSurpCount,0)
                .setSql("surp_count = surp_count - 1");
        return this.update(updateWrapper);
    }

    @Override
    public List<ActivityReward> getActivityRewards(Integer activityId, Integer activityRuleId) {
        QueryWrapper<ActivityReward> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ActivityReward::getActivityId,activityId).eq(ActivityReward::getActivityRuleId,activityRuleId);
        return this.list(queryWrapper);
    }
}
