package com.console.activity.task.handler.vipCheckIn;

import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;

public class VipCheckInActivityHandler extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.VIP_CHECK_IN;
    }
}
