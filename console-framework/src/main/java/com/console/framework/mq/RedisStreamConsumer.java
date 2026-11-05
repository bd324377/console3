package com.console.framework.mq;

import com.console.framework.config.DynamicTableNameHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.RecordId;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

/** 新消息与重新接管的消息都经过此入口。业务提交后才确认。 */
@Component
public class RedisStreamConsumer {
    private static final Logger log = LoggerFactory.getLogger(RedisStreamConsumer.class);
    private final StringRedisTemplate redis;
    private final ConsumerProvider config;
    private final Map<String, Handler> routes = new HashMap<>();
    private static final ObjectMapper JSON = new ObjectMapper();
    private final java.util.concurrent.atomic.LongAdder deadLetters = new java.util.concurrent.atomic.LongAdder();

    long deadLettersSinceStart() { return deadLetters.sum(); }

    /** 注册为 Spring Bean。handle 返回前须完成业务事务，按 tenantId + eventId 幂等。 */
    public interface Handler {
        String type();
        void handle(String eventId, Map<String, String> fields) throws Exception;
    }

    public RedisStreamConsumer(StringRedisTemplate redis, ConsumerProvider config, List<Handler> handlers,
            @Value("${redis.stream.required-types:}") String requiredTypes) {
        this.redis = redis;
        this.config = config;
        for (Handler handler : handlers) {
            if (handler.type() == null || handler.type().isBlank())
                throw new IllegalArgumentException("Handler 消息类型不能为空");
            if (routes.putIfAbsent(handler.type(), handler) != null)
                throw new IllegalArgumentException("重复的 MQ 消息类型：" + handler.type());
        }
        // 消费服务在所有副本配置同一清单。发布服务不配置该项。
        Set<String> required = Arrays.stream(requiredTypes.split(",")).map(String::trim)
                .filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        if (!required.isEmpty() && !routes.keySet().equals(required))
            throw new IllegalStateException("消费组 Handler 不匹配，预期 " + required + "，实际 " + routes.keySet());
    }

    boolean hasHandlers() {
        return !routes.isEmpty();
    }

    void process(MapRecord<String, String, String> record, long deliveries) {
        Map<String, String> fields = record.getValue();
        int tenant = parseTenant(fields.get("tenantId"));
        String event = fields.get("eventId");
        Handler handler = routes.get(fields.get("type"));
        if (tenant <= 0 || event == null || event.isBlank() || handler == null) {
            deadLetter(record, "无效的 tenantId、eventId 或不支持的 type");
            return;
        }

        try {
            invoke(handler, tenant, event, fields);
        } catch (Exception ex) {
            log.warn("业务处理失败 tenant={} event={} record={} deliveries={}", tenant, event, record.getId(), deliveries, ex);
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
            if (deliveries >= config.maxDeliveries) deadLetter(record, ex.getClass().getSimpleName());
            else scheduleRetry(record, deliveries);
            return; // 临时错误留在 PEL，等待恢复任务。
        }
        // ACK 异常独立于业务失败。即使网络超时，也不能将已经成功的业务送入死信。
        Long count = redis.opsForStream().acknowledge(config.group, record);
        if (!Long.valueOf(1).equals(count))
            log.warn("ACK 未命中 group={} record={} count={}", config.group, record.getId(), count);
    }

    private void invoke(Handler handler, int tenant, String event, Map<String, String> fields) throws Exception {
        Integer previous = DynamicTableNameHandler.getTenantId();
        long start = System.nanoTime();
        try {
            DynamicTableNameHandler.setTenantId(tenant);
            handler.handle(event, Map.copyOf(fields));
        } finally {
            if (previous == null) DynamicTableNameHandler.removeTenantId();
            else DynamicTableNameHandler.setTenantId(previous);
            long elapsed = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            if (elapsed >= config.slowHandlerMillis)
                log.warn("MQ Handler 耗时过长 type={} tenant={} event={} elapsedMs={}", handler.type(), tenant, event, elapsed);
        }
    }

    /**
     * 人工重放本组的一条死信（参数是死信 Stream 中的 ID）。使用保存的正文，
     * 即使源记录已裁剪也能重试。只调用本组 Handler，不向源 Stream 重新广播。
     * 成功后删除死信；异常时保留。提交后删除前宕机仍可能重复，沿用原 eventId 去重。
     */
    public boolean replayDeadLetter(RecordId deadId) throws Exception {
        String key = config.deadLetterStream();
        var rows = redis.<String, String>opsForStream().range(key, Range.closed(deadId.getValue(), deadId.getValue()));
        if (rows == null) throw new IllegalStateException("死信重放不支持 Redis 管线或事务");
        if (rows.isEmpty()) return false;
        Map<String, String> envelope = rows.get(0).getValue();
        if (!config.stream.equals(envelope.get("source")) || !config.group.equals(envelope.get("group")))
            throw new IllegalArgumentException("死信来源与当前订阅不符");
        // Lua 保存的是 Redis 原始字段序列 [key,value,...]，所有值均按字符串还原。
        String[] pairs = JSON.readValue(envelope.get("fields"), String[].class);
        if (pairs.length % 2 != 0) throw new IllegalArgumentException("死信字段不完整");
        Map<String, String> fields = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) fields.put(pairs[i], pairs[i + 1]);
        Handler handler = routes.get(fields.get("type"));
        int tenant = parseTenant(fields.get("tenantId"));
        String event = fields.get("eventId");
        if (handler == null || tenant <= 0 || event == null || event.isBlank())
            throw new IllegalStateException("当前 Handler 或消息字段不支持重放");
        invoke(handler, tenant, event, fields);
        redis.opsForStream().delete(key, deadId);
        log.info("死信重放完成 group={} deadId={} event={}", config.group, deadId, event);
        return true;
    }

    private static int parseTenant(String value) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ex) { return 0; }
    }

    // 仅对明确失败的消息调整 idle，使其提前达到接管阈值。慢任务仍使用完整失联超时。
    // 在同一脚本中校验所有者；JUSTID 保持投递次数，计数由实际恢复接管增加。
    private static final DefaultRedisScript<Long> RETRY = new DefaultRedisScript<>("""
            local p = redis.call('XPENDING', KEYS[1], ARGV[1], ARGV[2], ARGV[2], 1)
            if #p == 0 or p[1][2] ~= ARGV[3] then return 0 end
            local ids = redis.call('XCLAIM', KEYS[1], ARGV[1], ARGV[3], 0, ARGV[2],
                                   'IDLE', ARGV[4], 'JUSTID')
            return #ids
            """, Long.class);

    private void scheduleRetry(MapRecord<String, String, String> record, long deliveries) {
        long multiplier = 1L << Math.min(deliveries - 1, 5);
        long delay = config.retryMillis > config.idleMillis / multiplier
                ? config.idleMillis : config.retryMillis * multiplier;
        redis.execute(RETRY, List.of(config.stream), config.group, record.getId().getValue(),
                config.getConsumerName(), Long.toString(config.idleMillis - delay));
    }

    // 检查 PEL 所有者，防止旧消费者把已接管或已确认的消息再转为死信。
    private static final DefaultRedisScript<Long> MOVE_TO_DEAD = new DefaultRedisScript<>("""
            local p = redis.call('XPENDING', KEYS[1], ARGV[1], ARGV[2], ARGV[2], 1)
            if #p == 0 or p[1][2] ~= ARGV[3] then return 0 end
            local body = redis.call('XRANGE', KEYS[1], ARGV[2], ARGV[2])
            if #body == 0 then return -1 end
            redis.call('XADD', KEYS[2], '*', 'source', KEYS[1], 'group', ARGV[1],
                       'recordId', ARGV[2], 'reason', ARGV[4], 'deliveries', p[1][4],
                       'fields', cjson.encode(body[1][2]))
            return redis.call('XACK', KEYS[1], ARGV[1], ARGV[2])
            """, Long.class);

    private void deadLetter(MapRecord<String, String, String> record, String reason) {
        Long moved = redis.execute(MOVE_TO_DEAD, List.of(config.stream, config.deadLetterStream()),
                config.group, record.getId().getValue(), config.getConsumerName(), reason);
        if (Long.valueOf(1).equals(moved)) deadLetters.increment();
        log.warn("死信转移 group={} record={} result={} reason={}", config.group, record.getId(), moved, reason);
    }
}
