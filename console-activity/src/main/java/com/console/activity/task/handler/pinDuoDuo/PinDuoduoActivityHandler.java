package com.console.activity.task.handler.pinDuoDuo;

import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PinDuoduoActivityHandler extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.PIN_DUO_DUO;
    }
}
