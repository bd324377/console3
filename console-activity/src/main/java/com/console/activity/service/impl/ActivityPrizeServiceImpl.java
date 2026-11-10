package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.activity.entity.ActivityPrize;
import com.console.activity.mapper.ActivityPrizeMapper;
import com.console.activity.service.ActivityPrizeService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityPrizeServiceImpl extends BaseServiceImpl<ActivityPrizeMapper, ActivityPrize> implements ActivityPrizeService {

    @Override
    public boolean deductPrizeSurplusCount(Integer activityPrizeId) {
        UpdateWrapper<ActivityPrize> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(ActivityPrize::getId,activityPrizeId).gt(ActivityPrize::getSurpCount,0)
                .setSql("surp_count = surp_count - 1");
        return this.update(updateWrapper);
    }

    @Override
    public List<ActivityPrize> getActivityPrizes(Integer activityId, Integer activityRuleId) {
        QueryWrapper<ActivityPrize> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ActivityPrize::getActivityId,activityId).eq(ActivityPrize::getActivityRuleId,activityRuleId);
        return this.list(queryWrapper);
    }
}
