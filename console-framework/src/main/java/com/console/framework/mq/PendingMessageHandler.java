package com.console.framework.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

/** 按游标分段扫描。限制每轮页数和耗时，下轮从上次访问的位置继续。 */
@Component
public class PendingMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(PendingMessageHandler.class);
    private static final int PAGE_SIZE = 100;
    private final StringRedisTemplate redis;
    private final ConsumerProvider config;
    private final RedisStreamConsumer consumer;
    private String cursor;

    public PendingMessageHandler(StringRedisTemplate redis, ConsumerProvider config, RedisStreamConsumer consumer) {
        this.redis = redis;
        this.config = config;
        this.consumer = consumer;
    }

    @Scheduled(fixedDelayString = "${redis.stream.recovery-delay-ms:2000}")
    public void handlePendingMessages() {
        if (!consumer.hasHandlers()) return;
        try {
            long deadline = System.nanoTime() + Duration.ofSeconds(2).toNanos();
            for (int i = 0; i < 5 && System.nanoTime() < deadline; i++) {
                if (!scanPage(deadline)) break;
            }
        } catch (RuntimeException ex) {
            log.warn("Pending 扫描失败，下轮重试 group={}", config.group, ex);
        }
    }

    private boolean scanPage(long deadline) {
        // 使用包含起点的范围并显式跳过游标，兼容支持 Streams 的 Redis 5+。
        Range<String> range = cursor == null ? Range.unbounded()
                : Range.of(Range.Bound.inclusive(cursor), Range.Bound.unbounded());
        int count = cursor == null ? PAGE_SIZE : PAGE_SIZE + 1;
        var page = redis.opsForStream().pending(config.stream, config.group, range, count);
        String previous = cursor;
        int visited = 0;
        for (PendingMessage message : page) {
            if (System.nanoTime() >= deadline || Thread.currentThread().isInterrupted()) return false;
            visited++;
            cursor = message.getIdAsString();
            if (cursor.equals(previous)) continue;
            try {
                reclaim(message);
            } catch (RuntimeException ex) {
                log.warn("恢复单条消息失败 group={} record={}", config.group, cursor, ex);
            }
        }
        if (visited < count) cursor = null;
        return cursor != null;
    }

    private void reclaim(PendingMessage message) {
        Duration idle = Duration.ofMillis(config.idleMillis);
        if (message.getElapsedTimeSinceLastDelivery().compareTo(idle) < 0) return;
        var records = redis.<String,String>opsForStream().claim(config.stream, config.group,
                config.getConsumerName(), idle, message.getId());
        for (var record : records) consumer.process(record, message.getTotalDeliveryCount() + 1);
    }
}
