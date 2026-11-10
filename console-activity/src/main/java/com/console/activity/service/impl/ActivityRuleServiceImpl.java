package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.activity.dto.req.ActivityRuleReq;
import com.console.activity.entity.ActivityRule;
import com.console.activity.mapper.ActivityRuleMapper;
import com.console.activity.service.ActivityRuleService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityRuleServiceImpl extends BaseServiceImpl<ActivityRuleMapper, ActivityRule> implements ActivityRuleService {

//    @Override
//    public boolean addActivityRule(ActivityRuleReq.AddActivityRuleReq activityRuleReq) {
//
//    }

    @Override
    public List<ActivityRule> getActivityRules(Integer activityId) {
        QueryWrapper<ActivityRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,rule_name,activity_id,trigger_type,receive_type,rule_trigger_threshold,rule_key")
                .lambda().eq(ActivityRule::getActivityId,activityId);
        return this.list(queryWrapper);
    }
}
