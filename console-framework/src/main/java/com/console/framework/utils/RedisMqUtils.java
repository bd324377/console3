package com.console.framework.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisMqUtils {

    private static StringRedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate template;
    @PostConstruct
    public void init() {
        redisTemplate = template;
    }
    public static void acknowledge(String STREAM_KEY, String GROUP, RecordId recordId) {
        try {
            redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP, recordId);
        } catch (Exception e) {
            log.error("Failed to ack message: {}", recordId, e);
        }
    }
}
