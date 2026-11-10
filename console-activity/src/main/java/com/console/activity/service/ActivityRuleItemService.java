package com.console.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.activity.entity.ActivityRuleItem;

import java.util.List;

public interface ActivityRuleItemService extends IService<ActivityRuleItem> {
    List<ActivityRuleItem> getActivityRuleItems(Integer activityId, Integer activityRuleId);
}
