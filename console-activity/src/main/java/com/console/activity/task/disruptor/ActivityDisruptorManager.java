package com.console.activity.task.disruptor;

import com.console.framework.constants.ActivityEvent;
import com.console.framework.constants.ActivityType;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
public class ActivityDisruptorManager implements SmartLifecycle {
    @Resource
    private ActivityDispatcher dispatcher;
    private volatile boolean running;
    private boolean stopping;
    private final ReadWriteLock queueLock = new ReentrantReadWriteLock();
    private final Map<ActivityType, ActivityQueue> queues = new EnumMap<>(ActivityType.class);

    @Value("${activity.disruptor.buffer-size:2048}")
    private int bufferSize;
    @Value("${activity.disruptor.shutdown-timeout-seconds:10}")
    private long shutdownTimeoutSeconds;


    @Override
    public void start() {
        queueLock.writeLock().lock();
        try {
            if (running) {
                return;
            }
            if (stopping) {
                throw new IllegalStateException("活动队列正在关闭");
            }
            if (bufferSize <= 0 || Integer.bitCount(bufferSize) != 1 || shutdownTimeoutSeconds <= 0) {
                throw new IllegalArgumentException("活动队列容量必须为 2 的幂，关闭超时必须为正数");
            }

            try {
                for (ActivityType type : dispatcher.getActivityTypes()) {
                    ActivityQueue queue = createQueue();
                    queues.put(type, queue);
                    queue.disruptor.start();
                }
                running = true;
            } catch (RuntimeException | Error failure) {
                for (ActivityQueue queue : queues.values()) {
                    queue.disruptor.halt();
                }
                queues.clear();
                throw failure;
            }
        } finally {
            queueLock.writeLock().unlock();
        }
    }

    public CompletableFuture<Object> publishActivityEvent(ActivityEvent activityEvent) {
        if (activityEvent == null || activityEvent.getActivityType() == null || activityEvent.getActivityId() == null
                || activityEvent.getTenantId() == null || activityEvent.getEventType() == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("活动类型、活动 ID、租户 ID 和操作类型不能为空")
            );
        }

        ActivityType type = activityEvent.getActivityType();
        CompletableFuture<Object> resultFuture = new CompletableFuture<>();

        queueLock.readLock().lock();
        try {
            if (!running) {
                return CompletableFuture.failedFuture(
                        new RejectedExecutionException("Activity queues are stopped")
                );
            }

            ActivityQueue queue = queues.get(type);
            if (queue == null) {
                return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Unsupported activity type: " + type)
                );
            }

            queue.pending.add(resultFuture);
            resultFuture.whenComplete(
                    (result, failure) -> queue.pending.remove(resultFuture)
            );

            boolean published = queue.ringBuffer.tryPublishEvent(
                    (target, sequence) -> {
                        target.setEventId(activityEvent.getEventId());
                        target.setActivityId(activityEvent.getActivityId());
                        target.setActivityType(type);
                        target.setUserId(activityEvent.getUserId());
                        target.setParam(activityEvent.getParam());
                        target.setTenantId(activityEvent.getTenantId());
                        target.setEventType(activityEvent.getEventType());
                        target.setResultFuture(resultFuture);
                    }
            );

            if (!published) {
                resultFuture.completeExceptionally(
                        new RejectedExecutionException("Activity queue is full: " + type)
                );
            }

            return resultFuture;
        } finally {
            queueLock.readLock().unlock();
        }
    }

    @Override
    public void stop() {
        List<ActivityQueue> queuesToStop;

        queueLock.writeLock().lock();
        try {
            if (!running) {
                return;
            }

            running = false;
            stopping = true;
            queuesToStop = new ArrayList<>(queues.values());
            queues.clear();
        } finally {
            queueLock.writeLock().unlock();
        }

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(shutdownTimeoutSeconds);

        try {
            for (ActivityQueue queue : queuesToStop) {
                stopQueue(queue, Math.max(0L, deadline - System.nanoTime()));
            }
        } finally {
            queueLock.writeLock().lock();
            try {
                stopping = false;
            } finally {
                queueLock.writeLock().unlock();
            }
        }
    }

    private void stopQueue(ActivityQueue queue, long remainingNanos) {
        try {
            queue.disruptor.shutdown(remainingNanos, TimeUnit.NANOSECONDS);
        } catch (TimeoutException failure) {
            log.warn("活动队列关闭超时", failure);
            queue.disruptor.halt();
            for (CompletableFuture<Object> future : queue.pending) {
                future.completeExceptionally(new RejectedExecutionException("活动队列关闭超时，执行结果未知", failure));
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private ActivityQueue createQueue() {
        Disruptor<ActivityEvent> disruptor = new Disruptor<>(
                ActivityEvent::new,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.MULTI,
                new BlockingWaitStrategy()
        );

        disruptor.setDefaultExceptionHandler(new ActivityExceptionHandler());
        disruptor.handleEventsWith(new ActivityDispatchHandler(dispatcher));

        return new ActivityQueue(disruptor);
    }

    private static final class ActivityQueue {
        private final Disruptor<ActivityEvent> disruptor;
        private final RingBuffer<ActivityEvent> ringBuffer;
        private final Set<CompletableFuture<Object>> pending = ConcurrentHashMap.newKeySet();

        private ActivityQueue(Disruptor<ActivityEvent> disruptor) {
            this.disruptor = disruptor;
            this.ringBuffer = disruptor.getRingBuffer();
        }
    }
}
