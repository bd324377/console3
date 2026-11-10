package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.activity.entity.ActivityPrize;
import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.mapper.ActivityRuleItemMapper;
import com.console.activity.service.ActivityRuleItemService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityRuleItemServiceImpl extends BaseServiceImpl<ActivityRuleItemMapper, ActivityRuleItem> implements ActivityRuleItemService {
    @Override
    public List<ActivityRuleItem> getActivityRuleItems(Integer activityId, Integer activityRuleId) {
        QueryWrapper<ActivityRuleItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ActivityRuleItem::getActivityId,activityId).eq(ActivityRuleItem::getActivityRuleId,activityRuleId);
        return this.list(queryWrapper);
    }
}
