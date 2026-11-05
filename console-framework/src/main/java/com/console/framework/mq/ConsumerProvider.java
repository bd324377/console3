package com.console.framework.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.UUID;

/** 同一进程内新消息和 Pending 恢复共用身份；重启后由新实例接管遗留消息。 */
@Component
public class ConsumerProvider {
    final String stream;
    final String group;
    final long idleMillis;
    final int maxDeliveries;
    final long retryMillis;
    final long slowHandlerMillis;
    private final String name;

    public ConsumerProvider(@Value("${redis.stream.key:redisStreamMq}") String stream,
            @Value("${redis.stream.group:redisStreamMq-channel}") String group,
            @Value("${redis.stream.consumer-prefix:consumer}") String prefix,
            @Value("${redis.stream.claim-idle-ms:300000}") long idleMillis,
            @Value("${redis.stream.max-deliveries:5}") int maxDeliveries,
            @Value("${redis.stream.retry-delay-ms:3000}") long retryMillis,
            @Value("${redis.stream.slow-handler-ms:5000}") long slowHandlerMillis) {
        if (stream.isBlank() || group.isBlank() || idleMillis < 1 || maxDeliveries < 1 || retryMillis < 1 || slowHandlerMillis < 1)
            throw new IllegalArgumentException("Redis Stream 配置无效");
        this.stream = stream;
        this.group = group;
        this.idleMillis = idleMillis;
        this.maxDeliveries = maxDeliveries;
        this.retryMillis = retryMillis;
        this.slowHandlerMillis = slowHandlerMillis;
        name = prefix + "-" + System.getProperty("instance.id", "worker") + "-" + UUID.randomUUID();
        if (stream.contains("{") || stream.contains("}")) {
            int start = stream.indexOf('{');
            if (start < 0 || stream.indexOf('}', start) <= start + 1)
                throw new IllegalArgumentException("Stream 的 Redis Cluster hash tag 无效");
        }
    }

    public String getConsumerName() { return name; }

    String deadLetterStream() {
        // 与源 Stream 同槽，允许 Lua 原子完成死信转移和 ACK。
        return (stream.contains("{") ? stream : "{" + stream + "}") + ":dead:" + group;
    }
}
