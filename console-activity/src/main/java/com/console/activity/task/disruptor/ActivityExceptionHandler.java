package com.console.activity.task.disruptor;

import com.console.framework.constants.ActivityEvent;
import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ActivityExceptionHandler implements ExceptionHandler<ActivityEvent> {
    @Override
    public void handleEventException(Throwable failure, long sequence, ActivityEvent activityEvent) {
        if (activityEvent == null) {
            log.error("Activity consumer failed, sequence={}", sequence, failure);
            return;
        }

        CompletableFuture<Object> resultFuture = activityEvent.getResultFuture();

        log.error(
                "Activity consumer failed, sequence={}, eventId={}, activityType={}",
                sequence,
                activityEvent.getEventId(),
                activityEvent.getActivityType(),
                failure
        );

        activityEvent.clear();

        if (resultFuture != null) {
            resultFuture.completeExceptionally(failure);
        }
    }

    @Override
    public void handleOnStartException(Throwable failure) {
        log.error("Activity consumer failed to start", failure);
    }

    @Override
    public void handleOnShutdownException(Throwable failure) {
        log.error("Activity consumer failed to shut down", failure);
    }
}
