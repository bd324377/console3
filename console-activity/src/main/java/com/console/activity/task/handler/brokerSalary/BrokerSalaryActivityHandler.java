package com.console.activity.task.handler.brokerSalary;

import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BrokerSalaryActivityHandler extends ActivityStrategy {
    @Override
    public ActivityType getActivityType() {
        return ActivityType.BROKER_SALARY;
    }
}
