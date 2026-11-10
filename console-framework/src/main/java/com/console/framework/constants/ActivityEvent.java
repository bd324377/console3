package com.console.framework.constants;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class ActivityEvent {
    public static final int TRIGGER = 1;
    public static final int RECEIVE_AWARD = 2;
    public static final int GET_DETAILS = 3;

    private Long    eventId;
    private Integer activityId;
    private ActivityType activityType;
    private Integer userId;
    private Object  param;
    private Integer tenantId;
    private Integer eventType = TRIGGER;
    private CompletableFuture<Object> resultFuture;

    public ActivityEvent(Integer activityId,ActivityType activityType,Integer userId,Integer tenantId,Object param) {
        this.eventId = IdWorker.getId();
        this.activityId = activityId;
        this.activityType = activityType;
        this.userId = userId;
        this.param = param;
        this.tenantId = tenantId;
    }

    public void clear() {
        setEventId(null);
        setActivityId(null);
        setActivityType(null);
        setUserId(null);
        setParam(null);
        setTenantId(null);
        setEventType(null);
        setResultFuture(null);
    }
}
