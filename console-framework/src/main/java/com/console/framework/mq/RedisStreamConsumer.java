package com.console.framework.mq;

import com.console.framework.utils.RedisMqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RedisStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {
    private static final String STREAM_KEY = "CONSOLE:REDIS_MQ";
    private static final String GROUP = "pay-group";
//    @Resource
//    private CacheClearHandler cacheClearHandler;
    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        Map<String, String> msgMap = record.getValue();//消息内容
        String type = msgMap.get("type");//消息类型
        if (type == null) {
            log.warn("Missing type field, message id: {}", record.getId());
            RedisMqUtils.acknowledge(STREAM_KEY, GROUP, record.getId());
            return;
        }
        try {
            switch (type) {
//                case "CACHE_CLEAR":// 这里也可以路由到缓存清理，但点对点场景下缓存清理只由一个实例执行
//                    cacheClearHandler.handle(msgMap);
//                    break;
                default:
                    log.warn("Unknown message type: {}", type);
            }
            RedisMqUtils.acknowledge(STREAM_KEY, GROUP, record.getId());
        } catch (Exception e) {
            log.error("Error processing message id={}, type={}", record.getId(), type, e);
            // 根据业务选择：确认（丢弃）或转入死信队列，这里简单确认避免阻塞
            acknowledge(record);
        }
    }

    private void acknowledge(MapRecord<String, String, String> record) {
        try {
            RedisMqUtils.acknowledge(STREAM_KEY, GROUP, record.getId());
        } catch (Exception e) {
            log.error("Failed to ack message: {}", record.getId(), e);
        }
    }
}
