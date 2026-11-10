package com.console.activity.task.handler.checkIn;

import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;

public class CheckInActivityHandler extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.SIGN_IN;
    }
}
