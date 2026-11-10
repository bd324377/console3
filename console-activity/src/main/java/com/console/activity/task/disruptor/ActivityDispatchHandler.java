package com.console.activity.task.disruptor;

import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ActivityEvent;
import com.lmax.disruptor.EventHandler;

import java.util.concurrent.CompletableFuture;

public class ActivityDispatchHandler implements EventHandler<ActivityEvent> {
    private final ActivityDispatcher activityDispatcher;

    public ActivityDispatchHandler(ActivityDispatcher activityDispatcher) {
        this.activityDispatcher = activityDispatcher;
    }

    @Override
    public void onEvent(ActivityEvent activityEvent, long sequence, boolean endOfBatch) {
        CompletableFuture<Object> resultFuture = activityEvent.getResultFuture();
        Object result;
        try {
            DynamicTableNameHandler.setTenantId(activityEvent.getTenantId());
            result = activityDispatcher.dispatch(activityEvent);
        } finally {
            DynamicTableNameHandler.removeTenantId();
        }
        activityEvent.clear();
        resultFuture.complete(result);
    }
}
