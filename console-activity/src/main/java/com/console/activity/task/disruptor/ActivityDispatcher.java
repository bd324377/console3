package com.console.activity.task.disruptor;

import com.console.activity.task.handler.ActivityStrategy;
import com.console.framework.constants.ActivityEvent;
import com.console.framework.constants.ActivityType;
import com.console.framework.domain.BusinessException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ActivityDispatcher {
    private final Map<ActivityType, ActivityStrategy> strategies;

    public ActivityDispatcher(List<ActivityStrategy> activityStrategies) {
        Map<ActivityType, ActivityStrategy> registry = new EnumMap<>(ActivityType.class);
        for (ActivityStrategy strategy : activityStrategies) {
            ActivityType type = strategy.getActivityType();
            if (type == null || type == ActivityType.UNDEFINED) {
                throw new IllegalStateException("活动策略未指定有效类型");
            }
            if (registry.putIfAbsent(type, strategy) != null) {
                throw new IllegalStateException("重复注册活动策略：" + type);
            }
        }
        strategies = Map.copyOf(registry);
    }

    public Set<ActivityType> getActivityTypes() {
        return strategies.keySet();
    }

    public Object dispatch(ActivityEvent event) {
        ActivityStrategy strategy = strategies.get(event.getActivityType());
        if (strategy == null) {
            throw new BusinessException(400, "不支持的活动类型");
        }
        return switch (event.getEventType()) {
            case ActivityEvent.TRIGGER -> strategy.triggerActivity(event.getActivityId(), event.getUserId(), event.getParam());
            case ActivityEvent.RECEIVE_AWARD -> strategy.receiveActivityAward(event.getActivityId(), event.getUserId(), event.getParam());
            case ActivityEvent.GET_DETAILS -> strategy.getActivityDetails(event.getActivityId());
            default -> throw new BusinessException(400, "不支持的活动操作");
        };
    }
}
