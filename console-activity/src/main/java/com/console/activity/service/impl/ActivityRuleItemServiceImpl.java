package com.console.activity.service.impl;

import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.mapper.ActivityRuleItemMapper;
import com.console.activity.service.ActivityRuleItemService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ActivityRuleItemServiceImpl extends BaseServiceImpl<ActivityRuleItemMapper, ActivityRuleItem> implements ActivityRuleItemService {
}
