package com.console.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.activity.entity.ActivityRule;

import java.util.List;

public interface ActivityRuleService extends IService<ActivityRule> {
    List<ActivityRule> getActivityRules(Integer activityId);
}
