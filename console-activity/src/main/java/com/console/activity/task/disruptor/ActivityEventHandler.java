package com.console.activity.task.disruptor;

import com.console.framework.constants.ActivityEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivityEventHandler {
    @Resource
    private ActivityDisruptorManager activityDisruptorManager;

    @Async
    @EventListener(ActivityEvent.class)
    public void handleActivityEvent(ActivityEvent activityEvent) {
        activityDisruptorManager.publishActivityEvent(activityEvent) .whenComplete((result, failure) -> {
            if (failure != null) {
                log.error( "Activity:{} execution failed, eventId={}", activityEvent.getActivityType().getValue() ,activityEvent.getEventId(),failure);
            }
        });
    }
}
