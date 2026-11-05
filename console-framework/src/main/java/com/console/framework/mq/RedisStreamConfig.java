package com.console.framework.mq;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** 一个专用轮询线程。连接中断后退避重试，恢复不依赖应用重启。 */
@Slf4j
@Component
public class RedisStreamConfig {
    private final ScheduledExecutorService poller = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "redis-stream-poller"));
    private final StringRedisTemplate redis;
    private final ConsumerProvider config;
    private final RedisStreamConsumer consumer;
    private volatile boolean closing;
    private boolean groupReady;
    private volatile int failures;

    public RedisStreamConfig(StringRedisTemplate redis, ConsumerProvider config, RedisStreamConsumer consumer) {
        this.redis = redis;
        this.config = config;
        this.consumer = consumer;
    }

    @PostConstruct
    public void start() {
        // 只发消息的服务不参与抢占，也不把其他服务的消息误转死信。
        if (consumer.hasHandlers()) poller.execute(this::poll);
        else log.info("未注册 MQ Handler，本实例仅提供消息发布");
    }


    @Component
    public static class Publisher {
        private final StringRedisTemplate redis;
        private final ConsumerProvider config;

        public Publisher(StringRedisTemplate redis, ConsumerProvider config) {
            this.redis = redis;
            this.config = config;
        }

        /** 同一业务重试须复用 eventId；返回值仅代表 Redis 已写入。 */
        public RecordId publish(int tenantId, String eventId, String type, Map<String, String> payload) {
            if (tenantId <= 0 || eventId == null || eventId.isBlank() || type == null || type.isBlank())
                throw new IllegalArgumentException("消息需要有效的 tenantId、eventId 和 type");
            Map<String, String> fields = new HashMap<>(Map.copyOf(payload));
            fields.put("tenantId", Integer.toString(tenantId));
            fields.put("eventId", eventId);
            fields.put("type", type);
            RecordId id = redis.<String, String>opsForStream().add(config.stream, fields);
            if (id == null) throw new IllegalStateException("未取得 Redis Stream 消息 ID");
            return id;
        }
    }

    private void poll() {
        if (closing) return;
        long delay = 0;
        try {
            if (!groupReady) ensureGroup();
            var records = redis.<String,String>opsForStream().read(
                    Consumer.from(config.group, config.getConsumerName()),
                    StreamReadOptions.empty().count(10).block(Duration.ofSeconds(1)),
                    StreamOffset.create(config.stream, ReadOffset.lastConsumed()));
            if (records != null) {
                for (var record : records) {
                    if (closing) break; // 未处理的批内记录保留在 PEL。
                    try {
                        consumer.process(record, 1);
                    } catch (RuntimeException ex) {
                        // ACK/死信写入结果未知，留给 PEL 恢复；继续处理已经投递的其他消息。
                        log.error("单条 MQ 消息处理异常 group={} id={}", config.group, record.getId(), ex);
                    }
                }
            }
            failures = 0;
        } catch (RuntimeException ex) {
            groupReady = false;
            delay = Math.min(30, 1L << Math.min(failures++, 5));
            if (!closing) log.warn("Redis Stream 轮询异常，{} 秒后重试", delay, ex);
        }
        synchronized (this) {
            if (!closing) poller.schedule(this::poll, delay, TimeUnit.SECONDS);
        }
    }

    private void ensureGroup() {
        try {
            // 新组从历史起点开始。已有组保留原进度，不重置 offset。
            redis.opsForStream().createGroup(config.stream, ReadOffset.from("0-0"), config.group);
        } catch (RuntimeException ex) {
            if (!isBusyGroup(ex)) throw ex;
        }
        groupReady = true;
    }

    private boolean isBusyGroup(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause.getMessage() != null && cause.getMessage().contains("BUSYGROUP")) return true;
        }
        return false;
    }

    @PreDestroy
    public void stop() {
        synchronized (this) {
            closing = true;
            poller.shutdown();
        }
        try {
            if (!poller.awaitTermination(30, TimeUnit.SECONDS)) poller.shutdownNow();
        } catch (InterruptedException ex) {
            poller.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
