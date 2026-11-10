package com.console.activity.task.handler.vipWeekLoseBack;

import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VipWeekLoseBackActivityHandler extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.VIP_WEEK_LOSE_BACK;
    }
}
