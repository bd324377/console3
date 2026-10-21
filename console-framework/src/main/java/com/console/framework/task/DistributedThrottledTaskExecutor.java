package com.console.framework.task;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * 分布式限频任务执行器。
 *
 * <p>专为"集群环境下定时执行业务处理"设计，核心能力：
 * <ul>
 *     <li>分布式锁：同一 key 在同一时刻只被一个节点执行</li>
 *     <li>最小执行间隔：同一 key 在间隔内不会被重复执行</li>
 *     <li>本地预检查：调度频繁时，绝大多数触发不访问 Redis</li>
 *     <li>成功后才更新时间：失败不阻塞下次重试</li>
 * </ul>
 *
 * <p>典型用法（配合 {@code @Scheduled}）：任务应尽量短小，执行时间小于 Redis 锁过期时间
 * **/

@Slf4j
@Component
public class DistributedThrottledTaskExecutor {
    /** 默认最小执行间隔（毫秒） */
    private static final long DEFAULT_MIN_HOLD_TIME_MS = 15_000L;

    /** Redis 中"最后执行时间"key 的过期时间：7 天 */
    private static final long DEFAULT_EXPIRE_SECONDS = 7 * 24 * 3600L;

    /**
     * Lua 脚本：原子地判断是否已达到最小执行间隔。
     * KEYS[1] = 最后执行时间 key
     * ARGV[1] = 当前时间戳（毫秒）
     * ARGV[2] = 最小间隔（毫秒）
     * 返回 1 表示允许执行，0 表示未到间隔。
     */
    private static final String CHECK_SCRIPT =
            "local last = redis.call('GET', KEYS[1]) " +
                    "if last == false then return 1 end " +
                    "local now = tonumber(ARGV[1]) " +
                    "local interval = tonumber(ARGV[2]) " +
                    "if now - tonumber(last) >= interval then return 1 end " +
                    "return 0";

    private final RedisLockRegistry redisLockRegistry;
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> checkScript;

    /**
     * 本地缓存：key -> 本地视角下最后一次成功执行的时间戳。
     * 仅为减少 Redis 访问的优化，权威判断在 Redis。
     */
    private final Cache<String, Long> localLastExecTimeCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    /** 异步执行专用线程池（可选使用） */
    private final ThreadPoolExecutor asyncExecutor;

    public DistributedThrottledTaskExecutor(RedisLockRegistry redisLockRegistry,
                                            StringRedisTemplate redisTemplate) {
        this.redisLockRegistry = redisLockRegistry;
        this.redisTemplate = redisTemplate;
        this.checkScript = new DefaultRedisScript<>(CHECK_SCRIPT, Long.class);

        int cores = Runtime.getRuntime().availableProcessors();
        this.asyncExecutor = new ThreadPoolExecutor(
                cores,
                cores * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new NamedThreadFactory("throttled-task-"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    // ==================== 同步 API ====================

    /** 使用默认最小间隔（15s）执行任务，无数据。 */
    public <T> void runIfAllowed(String taskName, String redisLockKey, Consumer<T> taskHandler) {
        runIfAllowed(taskName, redisLockKey, DEFAULT_MIN_HOLD_TIME_MS, taskHandler, null);
    }

    /** 使用指定最小间隔执行任务，无数据。 */
    public <T> void runIfAllowed(String taskName, String redisLockKey, Long minHoldTimeMs,
                                 Consumer<T> taskHandler) {
        runIfAllowed(taskName, redisLockKey, minHoldTimeMs, taskHandler, null);
    }

    /** 同步执行，异常向上抛出。 */
    public <T> void runIfAllowed(String taskName, String redisLockKey, Long minHoldTimeMs,
                                 Consumer<T> taskHandler, T taskData) {
        executeInternal(taskName, redisLockKey, minHoldTimeMs, taskHandler, taskData, false);
    }

    /** 同步执行，异常静默（只记录日志）。定时任务推荐使用。 */
    public <T> void runQuietly(String taskName, String redisLockKey, Long minHoldTimeMs,
                               Consumer<T> taskHandler, T taskData) {
        executeInternal(taskName, redisLockKey, minHoldTimeMs, taskHandler, taskData, true);
    }

    // ==================== 异步 API（可选） ====================

    /** 异步执行，立即返回 Future。适用于长任务不阻塞调度线程的场景。 */
    public <T> CompletableFuture<Void> runAsync(String taskName, String redisLockKey,
                                                Long minHoldTimeMs,
                                                Consumer<T> taskHandler, T taskData) {
        return CompletableFuture.runAsync(
                () -> executeInternal(taskName, redisLockKey, minHoldTimeMs, taskHandler, taskData, false),
                asyncExecutor);
    }

    // ==================== 核心实现 ====================

    private <T> void executeInternal(String taskName,
                                     String redisLockKey,
                                     Long minHoldTimeMs,
                                     Consumer<T> taskHandler,
                                     T taskData,
                                     boolean swallowException) {

        Assert.hasText(taskName, "taskName must not be empty");
        Assert.hasText(redisLockKey, "redisLockKey must not be empty");
        Assert.notNull(taskHandler, "taskHandler must not be null");

        long intervalMs = Objects.requireNonNullElse(minHoldTimeMs, DEFAULT_MIN_HOLD_TIME_MS);

        // ---------- 1) 本地预检查（零 Redis 访问） ----------
        Long localLast = localLastExecTimeCache.getIfPresent(redisLockKey);
        if (localLast != null && System.currentTimeMillis() - localLast < intervalMs) {
            log.debug("[{}] 本地预检查未到间隔，跳过", taskName);
            return;
        }

        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        log.debug("==> start {}", taskName);

        try {
            // ---------- 2) 获取分布式锁 ----------
            Lock lock = redisLockRegistry.obtain(redisLockKey);
            if (!lock.tryLock()) {
                log.debug("[{}] 未获取到分布式锁，跳过", taskName);
                return;
            }

            try {
                long now = System.currentTimeMillis();

                // ---------- 3) Redis 原子检查间隔 ----------
                Long canExecute = redisTemplate.execute(
                        checkScript,
                        Collections.singletonList(redisLockKey),
                        String.valueOf(now),
                        String.valueOf(intervalMs));

                if (canExecute == null || canExecute != 1L) {
                    log.debug("[{}] 未到执行间隔，跳过", taskName);
                    return;
                }

                // ---------- 4) 执行业务 ----------
                taskHandler.accept(taskData);

                // ---------- 5) 成功后才更新时间 ----------
                long finishedAt = System.currentTimeMillis();
                redisTemplate.opsForValue().set(
                        redisLockKey,
                        String.valueOf(finishedAt),
                        DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
                localLastExecTimeCache.put(redisLockKey, finishedAt);
                log.info("[{}] 执行成功，耗时 {} ms", taskName,stopwatch.getTotalTimeMillis());

            } finally {
                try {
                    lock.unlock();
                } catch (Exception unlockEx) {
                    log.warn("[{}] 释放锁异常", taskName, unlockEx);
                }
            }
        } catch (Exception ex) {
            log.error("[{}] 执行异常", taskName, ex);
            if (!swallowException) {
                if (ex instanceof RuntimeException re) {
                    throw re;
                }
                throw new IllegalStateException("[" + taskName + "] 执行异常", ex);
            }
        } finally {
            stopwatch.stop();
            log.debug("<== end {}: running time = {} ms", taskName,stopwatch.getTotalTimeMillis());
        }
    }

    @PreDestroy
    public void shutdown() {
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ==================== 内部工具 ====================

    private static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger seq = new AtomicInteger();

        NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, prefix + seq.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}

//runIfAllowed：只有满足“拿到锁 + 到达间隔”才执行——名字直接说明行为。
//runQuietly：执行但吞掉异常——定时任务首选。
//runAsync：异步执行——长任务首选。

//推荐用法
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class UserSyncJob {
//
//    private final DistributedThrottledTaskExecutor executor;
//    private final UserSyncService userSyncService;
//
//    @Scheduled(cron = "0 */1 * * * ?")
//    public void sync() {
//        executor.runQuietly(
//                "同步用户",
//                "lock:job:sync:user",
//                60_000L,
//                ignored -> userSyncService.syncAll(),
//                null);
//    }
//}
//用 runQuietly：定时任务失败不应该让调度线程抛异常，记录日志即可。
//minHoldTimeMs = 60_000L 与 cron 间隔一致，防止同一节点或不同节点在间隔内重复触发。
//集群中只有 1 个节点真正执行。

//长任务用异步
//长任务用 runAsync，调度线程立即释放，不影响其他 @Scheduled 任务。
//注意确保 RedisLockRegistry.expireAfter 大于任务最大耗时。
//@Scheduled(cron = "0 0 * * * ?")
//public void hourlyReport() {
//    executor.runAsync(
//                    "生成小时报表",
//                    "lock:job:report:hourly",
//                    3_600_000L,
//                    hour -> reportService.generate(hour),
//                    LocalDateTime.now())
//            .exceptionally(ex -> {
//                log.error("生成报表失败", ex);
//                return null;
//            });
//}

//手动触发 + 防重
//@RestController
//@RequiredArgsConstructor
//public class CacheController {
//
//    private final DistributedThrottledTaskExecutor executor;
//
//    @PostMapping("/cache/refresh")
//    public String refresh() {
//        executor.runQuietly(
//                "手动刷新缓存",
//                "lock:cache:refresh",
//                30_000L,
//                ignored -> cacheService.refreshAll(),
//                null);
//        return "ok";
//    }
//}