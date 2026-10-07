package com.console.framework.mq;

import com.console.framework.utils.RedisMqUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PendingMessageHandler {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ConsumerProvider consumerProvider;
//    @Resource
//    private CacheClearHandler cacheClearHandler;
    @Value("${redis.stream.key:redisStreamMq}")
    private String streamKey;
    @Value("${redis.stream.group:redisStreamMq-channel}")
    private String group;

    // 每 30 秒扫描一次
    @Scheduled(fixedDelay = 30_000)
    public void handlePendingMessages() {
        // 1. 获取 Pending 概要
        PendingMessagesSummary summary = stringRedisTemplate.opsForStream().pending(streamKey, group);
        if (summary.getTotalPendingMessages() == 0) return;

        // 2. 查询每条 Pending 消息（可分页）
        PendingMessages pendingMessages = stringRedisTemplate.opsForStream()
                .pending(streamKey, group, Range.unbounded(), 100);

        for (PendingMessage pm : pendingMessages) {
            // 3. 判断消息闲置时间
            if (pm.getElapsedTimeSinceLastDelivery().compareTo(Duration.ofMinutes(5)) > 0) {
                log.warn("消息 {} 超过 5 分钟未确认，尝试转移", pm.getIdAsString());
                // 4. XCLAIM 转移给当前活跃消费者
                String consumerName = consumerProvider.getConsumerName();
                List<MapRecord<String, Object, Object>> claimed = stringRedisTemplate.opsForStream()
                        .claim(streamKey, group, consumerName, Duration.ofMinutes(5),
                                RecordId.of(pm.getIdAsString()));
                if (!claimed.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : claimed) {
                        // 将 Map<String, Object> 转为 Map<String, String>，所有值本质都是字符串
                        Map<Object,Object> originalMap = record.getValue();
                        Map<String, String> msgMap = originalMap.entrySet().stream()
                                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                                .collect(Collectors.toMap( entry -> String.valueOf(entry.getKey()),
                                        entry -> String.valueOf(entry.getValue())
                                ));
                        log.debug("处理转移的消息: {}", msgMap);
                        String type = msgMap.get("type");//消息类型
                        // 处理业务逻辑..
                        switch (type) {
//                            case "CACHE_CLEAR":// 这里也可以路由到缓存清理，但点对点场景下缓存清理只由一个实例执行
//                                cacheClearHandler.handle(msgMap);
//                                break;
                            default:
                                log.warn("Unknown message type: {}", type);
                        }
                        // 处理成功后确认
                        RedisMqUtils.acknowledge(streamKey, group, record.getId());
                    }
                    log.info("已成功转移消息 {} 至消费者 {}", pm.getIdAsString(), consumerName);
                }
            }
        }
    }
}
