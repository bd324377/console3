package com.console.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.activity.entity.ActivityPrize;

import java.util.List;

public interface ActivityPrizeService extends IService<ActivityPrize> {
    boolean deductPrizeSurplusCount(Integer ActivityPrizeId);
    List<ActivityPrize> getActivityPrizes(Integer activityId, Integer activityRuleId);
}
