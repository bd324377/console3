package com.console.activity.task.handler.vipWeekSalary;

import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;

public class VipWeekSalaryActivityHandler extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.VIP_WEEK_SALARY;
    }
}
