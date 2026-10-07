package com.console.framework.mq;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class RedisStreamConfig {
    @Resource
    private ConsumerProvider consumerProvider;
    private final String streamKey = "redisStreamMq";
    private final String group = "redisStreamMq-channel";

    private ExecutorService streamExecutor;
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;

    /**
     * 创建线程池（Spring 管理生命周期）
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService streamExecutor() {
        streamExecutor = Executors.newFixedThreadPool(4);
        return streamExecutor;
    }

    /**
     * 创建消费者组（若不存在则创建）
     */
    @Bean
    public String initConsumerGroup(StringRedisTemplate stringRedisTemplate) {
        try {
            stringRedisTemplate.opsForStream().createGroup(streamKey, group);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String causeMessage = e.getCause() != null ? e.getCause().getMessage() :"";
            // 消费者组已存在，忽略；若为其他连接异常，可记录日志
            if (errorMessage != null && errorMessage.contains("BUSYGROUP") || causeMessage.contains("BUSYGROUP")) {
                log.info("Consumer group '{}' already exists, skip creation.", group);
            } else {
                log.error(e.getCause().getMessage());
                log.error("Failed to create consumer group '{}'", group, e);
            }
        }
        return "Group initialized";
    }


    /**
     * 创建并启动 Stream 监听容器（Spring 管理生命周期）
     */
    @Bean(destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamContainer(
            RedisConnectionFactory factory,
            RedisStreamConsumer consumerHandler,
            ExecutorService streamExecutor) {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .batchSize(10)
                .pollTimeout(Duration.ofSeconds(1))
                .executor(streamExecutor)
                .errorHandler(t -> log.error("Redis Stream 底层异常", t))
                .build();

        container = StreamMessageListenerContainer.create(factory, options);

        String consumerName = consumerProvider.getConsumerName();
        log.info("启动消费者: group={}, consumer={}", group, consumerName);

        // 订阅：手动确认模式（监听器中必须调用 acknowledge）
        container.receive(
                Consumer.from(group, consumerName),
                StreamOffset.create(streamKey, ReadOffset.from(">")),
                consumerHandler
        );

        container.start();
        log.info("Redis Stream 消费者容器启动成功");
        return container;
    }

    /**
     * 容器关闭时额外确保线程池终止（双重保险）
     */
    @PreDestroy
    public void destroy() {
        if (streamExecutor != null && !streamExecutor.isShutdown()) {
            streamExecutor.shutdownNow();
        }
    }


//    /**
//     * 创建 Stream 监听容器及订阅
//     */
//    @Bean
//    public Subscription subscription(RedisConnectionFactory factory,
//                                     RedisStreamConsumer consumerHandler) {
//        // 容器配置
//        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
//                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
//                        .builder()
//                        .batchSize(10)                              // 每次拉取消息数量
//                        .pollTimeout(Duration.ofSeconds(1))         // 拉取超时
//                        .executor(Executors.newFixedThreadPool(4))  // 有界线程池，避免无限膨胀
//                        .errorHandler(t -> log.error("Redis Stream 消费异常", t)) // 错误处理器
//                        .build();
//
//        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
//                StreamMessageListenerContainer.create(factory, options);
//
//        String consumerName = consumerProvider.getConsumerName();
//        log.info("启动消费者: group={}, consumer={}", group, consumerName);
//
//        // 订阅：只消费新分配给该消费者的消息（>），手动确认模式（默认）
//        Subscription subscription = container.receive(
//                Consumer.from(group, consumerName),
//                StreamOffset.create(streamKey, ReadOffset.from(">")),
//                consumerHandler
//        );
//
//        container.start();
//        return subscription;
//    }
}
