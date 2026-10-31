package com.console.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig {
    @Bean(name = "settlementExecutor")
    public ThreadPoolTaskExecutor asyncSettlementExecutor(
            @Value("${async.core-pool-size:8}") int corePoolSize,
            @Value("${async.max-pool-size:16}") int maxPoolSize,
            @Value("${async.queue-capacity:1000}") int queueCapacity,
            @Value("${async.keep-alive-seconds:60}") int keepAliveSeconds) {

        if (corePoolSize < 0 || maxPoolSize < corePoolSize
                || queueCapacity < 0 || keepAliveSeconds < 0) {
            throw new IllegalArgumentException("线程池参数不合法");
        }

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("async-settlement-executor-");

        // 优雅关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        // 不需要手动 executor.initialize(); Spring 会自动调用
        return executor;
    }
}
