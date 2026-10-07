package com.console.core.merger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class BatchMergerScheduler {
    @Resource
    private List<AbstractBatchMerger<?>> mergers;

    @Resource(name = "batchExecutor")
    private ThreadPoolTaskExecutor batchMergerExecutor;

    @Resource(name = "auxiliaryExecutor")
    private ThreadPoolTaskExecutor auxiliaryExecutor;

    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "merger-scheduler");
            t.setDaemon(true);
            return t;
        });

        for (AbstractBatchMerger<?> merger : mergers) {
            // 批量入库任务
            scheduler.scheduleAtFixedRate(() -> batchMergerExecutor.submit(merger::doBatchInsert), 0, merger.flushIntervalSeconds, TimeUnit.SECONDS);
            // 重试队列消费任务
            scheduler.scheduleAtFixedRate(() -> auxiliaryExecutor.submit(merger::consumeRetryQueue), merger.retryConsumeIntervalSeconds, merger.retryConsumeIntervalSeconds, TimeUnit.SECONDS);
            // 溢出文件迁移任务
            scheduler.scheduleAtFixedRate(() -> auxiliaryExecutor.submit(merger::migrateOverflowToRetryQueue), merger.overflowRecoverIntervalSeconds, merger.overflowRecoverIntervalSeconds, TimeUnit.SECONDS);
            // 监控快照（每5分钟）
            scheduler.scheduleAtFixedRate(() -> auxiliaryExecutor.submit(merger::printMetrics), 5, 5, TimeUnit.MINUTES);
        }
        log.info("BatchMergerScheduler 初始化完成，共管理 {} 个实体", mergers.size());
    }

    @PreDestroy
    public void destroy() {
        log.info("关闭 BatchMergerScheduler...");
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                scheduler.shutdownNow();
            }
        }
        for (AbstractBatchMerger<?> merger : mergers) {
            merger.shutdown();
        }
        batchMergerExecutor.shutdown();
        auxiliaryExecutor.shutdown();
        try {
            if (!batchMergerExecutor.getThreadPoolExecutor().awaitTermination(10, TimeUnit.SECONDS)) {
                batchMergerExecutor.getThreadPoolExecutor().shutdownNow();
            }
            if (!auxiliaryExecutor.getThreadPoolExecutor().awaitTermination(10, TimeUnit.SECONDS)) {
                auxiliaryExecutor.getThreadPoolExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            batchMergerExecutor.getThreadPoolExecutor().shutdownNow();
            auxiliaryExecutor.getThreadPoolExecutor().shutdownNow();
        }
        log.info("BatchMergerScheduler 关闭完成");
    }
}
