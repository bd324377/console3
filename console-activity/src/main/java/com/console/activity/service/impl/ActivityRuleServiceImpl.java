package com.console.activity.service.impl;

import com.console.activity.entity.ActivityRule;
import com.console.activity.mapper.ActivityRuleMapper;
import com.console.activity.service.ActivityRuleService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ActivityRuleServiceImpl extends BaseServiceImpl<ActivityRuleMapper, ActivityRule> implements ActivityRuleService {
}
