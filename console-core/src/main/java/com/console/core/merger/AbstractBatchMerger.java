package com.console.core.merger;

import com.alibaba.fastjson.JSON;
import com.console.core.entity.ErrorRecord;
import com.console.core.service.ErrorRecordService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Slf4j
public abstract class AbstractBatchMerger<T> {
    @Resource
    private RedisUtils redisUtils;//保证redistemplate在该方法前加载
    // ----- 配置（由子类通过 setter 注入，或使用默认值）-----
    protected Integer queueCapacity = 50000;
    protected Integer flushThreshold = 2000;
    protected Integer flushIntervalSeconds = 3;
    protected Integer maxBatchSize;
    protected Integer retryQueueMaxSize;
    protected Integer retryConsumeBatchSize;
    protected Integer retryConsumeIntervalSeconds;
    protected Integer maxRetryTimes;
    protected Integer maxConsumeBatchesPerRun;
    protected Integer overflowRecoverIntervalSeconds;
    protected Integer overflowRecoverLines;
    protected Integer redisBackupSliceSize;
    protected Integer backupKeyTtlSeconds;
    protected Integer recoverMaxOrders;
    protected Integer redisPopBatchSize;
    protected Long    migrationBackoffThreshold;
    protected Long    backoffMaxWaitMinutes;
    protected String  baseDataDir;
    protected Long    overflowFileMaxSizeBytes;

    protected String retryListKey;
    protected String backupKeyPrefix;

    protected final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    protected final BlockingQueue<T> batchInsertQueue = new LinkedBlockingQueue<>(queueCapacity);;
    protected final Object overflowLock = new Object();
    protected BufferedWriter overflowWriter;
    protected final Object archiveLock = new Object();
    protected BufferedWriter archiveWriter;
    protected String currentArchiveDate;

    //监控指标
    protected final AtomicLong submittedCount = new AtomicLong(0);
    protected final AtomicLong successCount = new AtomicLong(0);
    protected final AtomicLong failedCount = new AtomicLong(0);
    protected final AtomicLong droppedCount = new AtomicLong(0);

    // 迁移退避
    protected final AtomicLong consecutiveMigrationFailures = new AtomicLong(0);
    protected volatile long firstFailureTimestamp = 0L;

    // 线程池（由外部注入）
    @Resource(name = "batchExecutor")
    private Executor batchExecutor;
    // 防止阈值触发时重复提交任务
    private final AtomicBoolean batchSubmitted = new AtomicBoolean(false);

    @Resource
    protected ErrorRecordService errorRecordService;

    // ----- 抽象方法（子类实现）-----
    protected abstract int getRetryTimes(T entity);
    protected abstract RecordType getRecordType();
    protected abstract Class<T> getRecordClass();
    protected abstract void saveBatchIgnore(List<T> objectList);
    protected abstract void incrementRetryTimes(T entity);
    protected abstract boolean isFromRecovery(T entity);
    protected abstract void setFromRecovery(T entity, boolean fromRecovery);
    protected abstract String getBusinessKey(T entity);
    protected abstract Map<Integer,List<T>> getGroupMap(List<T> records);

    // ----- 初始化 -----
    @PostConstruct
    public final void init() {
        // 设置默认值（如果子类未注入）
        if (queueCapacity == null || queueCapacity == 0) queueCapacity = 50000;
        if (flushThreshold == null || flushThreshold == 0) flushThreshold = 2000;
        if (flushIntervalSeconds == null || flushIntervalSeconds == 0) flushIntervalSeconds = 3;
        if (maxBatchSize == null || maxBatchSize == 0) maxBatchSize = 500;
        if (retryQueueMaxSize == null || retryQueueMaxSize == 0) retryQueueMaxSize = 30000;
        if (retryConsumeBatchSize == null || retryConsumeBatchSize == 0) retryConsumeBatchSize = 1000;
        if (retryConsumeIntervalSeconds == null || retryConsumeIntervalSeconds == 0) retryConsumeIntervalSeconds = 10;
        if (maxRetryTimes == null || maxRetryTimes == 0) maxRetryTimes = 3;
        if (maxConsumeBatchesPerRun == null || maxConsumeBatchesPerRun == 0) maxConsumeBatchesPerRun = 10;
        if (overflowRecoverIntervalSeconds == null || overflowRecoverIntervalSeconds == 0) overflowRecoverIntervalSeconds = 30;
        if (overflowRecoverLines == null || overflowRecoverLines == 0) overflowRecoverLines = 2000;
        if (redisBackupSliceSize == null || redisBackupSliceSize == 0) redisBackupSliceSize = 200;
        if (backupKeyTtlSeconds == null || backupKeyTtlSeconds == 0) backupKeyTtlSeconds = 86400;
        if (recoverMaxOrders == null || recoverMaxOrders == 0) recoverMaxOrders = 20000;
        if (redisPopBatchSize == null || redisPopBatchSize == 0) redisPopBatchSize = 1000;
        if (migrationBackoffThreshold == null || migrationBackoffThreshold == 0) migrationBackoffThreshold = 5L;
        if (backoffMaxWaitMinutes == null || backoffMaxWaitMinutes == 0) backoffMaxWaitMinutes = 5L;
        if (!StringUtils.hasText(baseDataDir)) baseDataDir = "/data/merger";
        if (overflowFileMaxSizeBytes == null || overflowFileMaxSizeBytes == 0) overflowFileMaxSizeBytes = 1024 * 1024 * 1024L;

        this.retryListKey = "TRANSFER:" + getRecordType() + ":RETRY";
        this.backupKeyPrefix = "TRANSFER:" + getRecordType() + ":BACKUP:BATCH:";

        initOverflowWriter();
        initArchiveDir();
        recoverProcessingFilesOnStartup();
        recoverFromBackupOnStartup();

        log.info("{} 初始化完成，队列容量: {}", getRecordType(), queueCapacity);
    }

    // ----- 文件初始化 -----
    private void initOverflowWriter() {
        String filePath = baseDataDir + "/" + getRecordType() + "/overflow.log";
        synchronized (overflowLock) {
            try {
                Path path = Paths.get(filePath);
                if (path.getParent() != null) Files.createDirectories(path.getParent());
                overflowWriter = new BufferedWriter(new FileWriter(filePath, true));
                log.info("{} 溢出文件路径: {}", getRecordType(), path.toAbsolutePath());
            } catch (IOException e) {
                log.error("创建溢出文件失败: {}", filePath, e);
            }
        }
    }

    private void initArchiveDir() {
        String dirPath = baseDataDir + "/" + getRecordType() + "/archive";
        try {
            Files.createDirectories(Paths.get(dirPath));
            log.info("{} 归档目录: {}", getRecordType(), dirPath);
        } catch (IOException e) {
            log.error("创建归档目录失败: {}", dirPath, e);
        }
    }

    // ----- 启动恢复 -----
    private void recoverProcessingFilesOnStartup() {
        String dir = baseDataDir + "/" + getRecordType();
        File parent = new File(dir);
        if (!parent.exists()) return;
        File[] files = parent.listFiles((d, name) -> name.endsWith(".processing"));
        if (files == null) return;
        for (File pf : files) {
            log.warn("{} 发现残留 .processing 文件: {}", getRecordType(), pf.getName());
            int recovered = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(pf))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        T entity = JSON.parseObject(line, getRecordClass());
                        if (getRetryTimes(entity) >= maxRetryTimes) {
                            archiveFailedEntity(entity);
                            log.warn("实体重试次数超限，归档: {}", JSON.toJSONString(entity));
                            continue;
                        }
                        if (pushToRetryQueue(entity)) {
                            recovered++;
                        } else {
                            writeToOverflowFile(line);
                        }
                    } catch (Exception e) {
                        log.error("反序列化失败，丢弃行: {}", line, e);
                    }
                }
            } catch (IOException e) {
                log.error("读取 .processing 文件失败: {}", pf.getAbsolutePath(), e);
                try { Files.move(pf.toPath(), Paths.get(pf.getAbsolutePath() + ".corrupt")); } catch (IOException ignored) {}
                continue;
            }
            try { Files.deleteIfExists(pf.toPath()); } catch (IOException ignored) {}
            log.info("恢复 .processing 文件完成，迁移 {} 条", recovered);
        }
    }

    private void recoverFromBackupOnStartup() {
        log.info("{} 开始从 Redis 备份恢复...", getRecordType());
        int total = 0;
        try {
            List<String> keys = RedisUtils.scanKeys(backupKeyPrefix + "*", 1000);
            if (ObjectUtils.isEmpty(keys)) return;
            for (String key : keys) {
                if (total >= recoverMaxOrders) {
                    log.warn("恢复数量已达上限 {}", recoverMaxOrders);
                    break;
                }
                List<T> batch = popAllFromList(key);
                if (batch.isEmpty()) {
                    RedisUtils.del(key);
                    continue;
                }
                batch.forEach(e -> setFromRecovery(e, true));
                for (T entity : batch) {
                    if (!batchInsertQueue.offer(entity)) {
                        writeToOverflowFile(JSON.toJSONString(entity));
                    }
                }
                total += batch.size();
                RedisUtils.del(key);
            }
        } catch (Exception e) {
            log.error("备份恢复异常", e);
        }
        log.info("{} 备份恢复完成，共恢复 {} 条", getRecordType(), total);
    }



    // ----- 核心业务方法（供调度器调用）-----
    public final void doBatchInsert() {
        if (shuttingDown.get()) return;
        try {
            List<T> batch = new ArrayList<>();
            int drained = batchInsertQueue.drainTo(batch);
            if (drained == 0) return;

            boolean allRecovery = batch.stream().allMatch(this::isFromRecovery);
            String backupKey = null;
            if (!allRecovery) {
                backupKey = backupKeyPrefix + UUID.randomUUID();
                if (!backupBatchToRedis(backupKey, batch)) {
                    backupKey = null;
                    recordError("BATCH", "BACKUP_FAIL", JSON.toJSONString(batch), new RuntimeException("Redis备份失败"));
                } else {
                    RedisUtils.setExpire(backupKey, backupKeyTtlSeconds);
                }
            }

            Map<Integer, List<T>> groupMap = getGroupMap(batch);

            int fail = 0;
            for (Map.Entry<Integer, List<T>> entry : groupMap.entrySet()) {
                DynamicTableNameHandler.setTenantId(entry.getKey());
                List<T> list = entry.getValue();
                for (int i = 0; i < list.size(); i += maxBatchSize) {
                    int end = Math.min(i + maxBatchSize, list.size());
                    List<T> sub = list.subList(i, end);
                    try {
                        saveBatchIgnore(sub);
                        successCount.addAndGet(sub.size());
                    } catch (Exception e) {
                        fail += sub.size();
                        failedCount.addAndGet(sub.size());
                        for (T entity : sub) {
                            String bizKey = getBusinessKey(entity);
                            recordError(bizKey, "BATCH_INSERT", JSON.toJSONString(entity), e);
                            incrementRetryTimes(entity);
                            routeFailedEntity(entity);
                        }
                    }
                }
            }

            // 删除备份 Key（如果全部成功或失败已路由）
            if (backupKey != null && fail == 0) {
                RedisUtils.del(backupKey);
            } else if (backupKey != null) {
                log.warn("{} 存在失败订单，保留备份 Key: {}", getRecordType(), backupKey);
            }
        } catch (Exception e) {
            log.error("{} 批量插入顶层异常", getRecordType(), e);
            recordError("UNKNOWN", "BATCH_INSERT_TOP", "TOP_LEVEL", e);
        }
    }

    public final void consumeRetryQueue() {
        if (shuttingDown.get()) return;
        int batchCount = 0;
        while (batchCount < maxConsumeBatchesPerRun) {
            List<T> batch = popFromRedisList(retryConsumeBatchSize);
            if (batch.isEmpty()) break;
            batchCount++;
            int offered = 0;
            boolean queueFull = false;
            for (T entity : batch) {
                if (getRetryTimes(entity) >= maxRetryTimes) {
                    archiveFailedEntity(entity);
                    continue;
                }
                if (batchInsertQueue.offer(entity)) {
                    offered++;
                } else {
                    queueFull = true;
                    if (!pushToRetryQueue(entity)) {
                        writeToOverflowFile(JSON.toJSONString(entity));
                    }
                }
            }
            if (offered == 0 && !batch.isEmpty()) {
                log.debug("{} 主队列已满，暂停消费", getRecordType());
                break;
            }
            if (queueFull) log.debug("{} 主队列已满，部分订单放回", getRecordType());
        }
    }

    public final void migrateOverflowToRetryQueue() {
        if (shuttingDown.get()) return;
        // 退避检查
        long failures = consecutiveMigrationFailures.get();
        if (failures >= migrationBackoffThreshold) {
            long now = System.currentTimeMillis();
            if (firstFailureTimestamp == 0) firstFailureTimestamp = now;
            long maxWait = TimeUnit.MINUTES.toMillis(backoffMaxWaitMinutes);
            if (now - firstFailureTimestamp > maxWait) {
                log.info("{} 退避超时，重置失败计数", getRecordType());
                consecutiveMigrationFailures.set(0);
                firstFailureTimestamp = 0;
            } else {
                return;
            }
        }

        String overflowPath = baseDataDir + "/" + getRecordType() + "/overflow.log";
        File file = new File(overflowPath);
        if (!file.exists() || file.length() == 0) return;

        // 检查 Redis 可用性
        try { RedisUtils.getListSize(retryListKey); } catch (Exception e) {
            log.debug("Redis不可用，跳过迁移");
            return;
        }

        File processing = new File(overflowPath + ".processing");
        File remaining = new File(overflowPath + ".remaining");

        synchronized (overflowLock) {
            try {
                if (overflowWriter != null) {
                    overflowWriter.close();
                    overflowWriter = null;
                }
                Files.move(file.toPath(), processing.toPath(),
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.error("重命名失败，迁移终止", e);
                recreateOverflowWriter();
                consecutiveMigrationFailures.incrementAndGet();
                return;
            }
        }

        int migrated = 0, processed = 0;
        boolean queueFull = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(processing));
             BufferedWriter remainingWriter = new BufferedWriter(new FileWriter(remaining))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (processed >= overflowRecoverLines || queueFull) {
                    remainingWriter.write(line);
                    remainingWriter.newLine();
                    continue;
                }
                processed++;
                try {
                    T entity = JSON.parseObject(line, getRecordClass());
                    if (getRetryTimes(entity) >= maxRetryTimes) {
                        archiveFailedEntity(entity);
                        continue;
                    }
                    if (pushToRetryQueue(entity)) {
                        migrated++;
                    } else {
                        queueFull = true;
                        remainingWriter.write(line);
                        remainingWriter.newLine();
                    }
                } catch (Exception e) {
                    log.error("反序列化失败，丢弃: {}", line, e);
                    recordError("UNKNOWN", "DESERIALIZE_FAIL", line, e);
                }
            }
        } catch (IOException e) {
            log.error("读取 .processing 失败", e);
            consecutiveMigrationFailures.incrementAndGet();
            appendRemainingLines(processing);
            recreateOverflowWriter();
            return;
        } finally {
            try { Files.deleteIfExists(processing.toPath()); } catch (IOException ignored) {}
        }

        // 替换原文件
        synchronized (overflowLock) {
            try {
                if (overflowWriter != null) {
                    overflowWriter.close();
                    overflowWriter = null;
                }
                if (remaining.exists() && remaining.length() > 0) {
                    Files.move(remaining.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.deleteIfExists(file.toPath());
                    Files.deleteIfExists(remaining.toPath());
                }
            } catch (IOException e) {
                log.error("替换原文件失败", e);
                appendRemainingLines(remaining);
            }
        }

        recreateOverflowWriter();

        if (migrated == 0 && processed > 0) {
            consecutiveMigrationFailures.incrementAndGet();
        } else {
            consecutiveMigrationFailures.set(0);
        }
        if (migrated > 0) log.info("{} 迁移 {} 条至重试队列", getRecordType(), migrated);
    }

    // ----- 提交入口 -----
    public final void submit(T entity) {
        if (shuttingDown.get()) {
            log.warn("{} 正在关闭，拒绝提交", getRecordType());
            return;
        }
        incrementRetryTimes(entity);
        setFromRecovery(entity, false);
        if (batchInsertQueue.offer(entity)) {
            submittedCount.incrementAndGet();
            if (batchInsertQueue.size() >= flushThreshold) {
                batchExecutor.execute(() -> {
                    try {
                        doBatchInsert();
                    } finally {
                        batchSubmitted.set(false);
                    }
                });
            }
        } else {
            recordError(getBusinessKey(entity), "SUBMIT_QUEUE_FULL", JSON.toJSONString(entity),
                    new IllegalStateException("主队列已满"));
            routeFailedEntity(entity);
        }
    }

    // ----- 路由失败实体 -----
    protected boolean routeFailedEntity(T entity) {
        if (getRetryTimes(entity) >= maxRetryTimes) {
            archiveFailedEntity(entity);
            return true;
        }
        if (pushToRetryQueue(entity)) return true;
        String json = JSON.toJSONString(entity);
        boolean written = writeToOverflowFile(json);
        if (!written) {
            droppedCount.incrementAndGet();
            recordError(getBusinessKey(entity), "ROUTE_FAILED_DROPPED", json,
                    new RuntimeException("重试队列满且溢出文件写入失败，丢弃"));
        }
        return written;
    }

    // ----- Redis 重试队列操作 -----
    protected boolean pushToRetryQueue(T entity) {
        try {
            long size = RedisUtils.getListSize(retryListKey);
            if (size >= retryQueueMaxSize) {
                recordError(getBusinessKey(entity), "PUSH_RETRY_FULL", JSON.toJSONString(entity),
                        new RuntimeException("重试队列已满, size=" + size));
                return false;
            }
            RedisUtils.rightPushAll(retryListKey, List.of(entity));
            return true;
        } catch (Exception e) {
            recordError(getBusinessKey(entity), "PUSH_RETRY_EXCEPTION", JSON.toJSONString(entity), e);
            return false;
        }
    }

    protected List<T> popFromRedisList(int count) {
        try {
            return RedisUtils.leftPopListValue(retryListKey, count, getRecordClass());
        } catch (Exception e) {
            log.error("弹出重试队列失败", e);
            return Collections.emptyList();
        }
    }

    // ----- Redis 备份 -----
    protected boolean backupBatchToRedis(String key, List<T> batch) {
        try {
            for (int i = 0; i < batch.size(); i += redisBackupSliceSize) {
                int end = Math.min(i + redisBackupSliceSize, batch.size());
                RedisUtils.rightPushAll(key, new ArrayList<>(batch.subList(i, end)));
            }
            return true;
        } catch (Exception e) {
            log.error("备份失败", e);
            return false;
        }
    }

    protected List<T> popAllFromList(String key) {
        List<T> all = new ArrayList<>();
        try {
            while (true) {
                List<T> batch = RedisUtils.leftPopListValue(key, redisPopBatchSize, getRecordClass());
                if (ObjectUtils.isEmpty(batch)) break;
                all.addAll(batch);
                if (all.size() >= recoverMaxOrders * 2) break;
            }
        } catch (Exception e) {
            log.error("弹出列表失败", e);
        }
        return all;
    }

    // ----- 溢出文件写入 -----
    protected boolean writeToOverflowFile(String json) {
        // 检查文件大小上限
        File file = new File(baseDataDir + "/" + getRecordType() + "/overflow.log");
        if (file.exists() && file.length() >= overflowFileMaxSizeBytes) {
            droppedCount.incrementAndGet();
            recordError("UNKNOWN", "OVERFLOW_FILE_MAX_SIZE", json,
                    new IOException("溢出文件已达上限 " + overflowFileMaxSizeBytes + " bytes，丢弃"));
            return false;
        }

        synchronized (overflowLock) {
            if (overflowWriter == null) recreateOverflowWriter();
            if (overflowWriter == null) {
                droppedCount.incrementAndGet();
                recordError("UNKNOWN", "WRITE_OVERFLOW_WRITER_NULL", json,
                        new IOException("溢出文件写入器未初始化"));
                return false;
            }
            try {
                overflowWriter.write(json);
                overflowWriter.newLine();
                overflowWriter.flush();
                return true;
            } catch (IOException e) {
                droppedCount.incrementAndGet();
                recordError("UNKNOWN", "WRITE_OVERFLOW_IOEXCEPTION", json, e);
                try { overflowWriter.close(); } catch (IOException ignored) {}
                overflowWriter = null;
                return false;
            }
        }
    }

    protected void recreateOverflowWriter() {
        synchronized (overflowLock) {
            try {
                if (overflowWriter != null) overflowWriter.close();
                String path = baseDataDir + "/" + getRecordType() + "/overflow.log";
                Path p = Paths.get(path);
                if (p.getParent() != null) Files.createDirectories(p.getParent());
                overflowWriter = new BufferedWriter(new FileWriter(path, true));
            } catch (IOException e) {
                log.error("重建写入器失败", e);
                overflowWriter = null;
            }
        }
    }

    protected void appendRemainingLines(File source) {
        if (source == null || !source.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = r.readLine()) != null) {
                writeToOverflowFile(line);
            }
        } catch (IOException e) {
            log.error("追加剩余行失败", e);
        }
    }

    // ----- 归档 -----
    protected void archiveFailedEntity(T entity) {
        String json = JSON.toJSONString(entity);
        synchronized (archiveLock) {
            try {
                String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                if (!today.equals(currentArchiveDate)) {
                    if (archiveWriter != null) archiveWriter.close();
                    String dir = baseDataDir + "/" + getRecordType() + "/archive";
                    Files.createDirectories(Paths.get(dir));
                    archiveWriter = new BufferedWriter(new FileWriter(dir + "/" + today + ".log", true));
                    currentArchiveDate = today;
                }
                if (archiveWriter == null) {
                    writeToOverflowFile(json);
                    return;
                }
                archiveWriter.write(json);
                archiveWriter.newLine();
                archiveWriter.flush();
            } catch (IOException e) {
                log.error("归档失败", e);
                try { if (archiveWriter != null) archiveWriter.close(); } catch (IOException ignored) {}
                archiveWriter = null;
                currentArchiveDate = null;
                writeToOverflowFile(json);
            }
        }
    }

    // ----- 异常记录 -----
    protected void recordError(String businessKey, String operation, String dataSnapshot, Throwable ex) {
        if (errorRecordService == null) {
            log.warn("ErrorRecordService 未注入，无法记录异常: {}", ex.getMessage());
            return;
        }
        try {
            ErrorRecord errorRecord = new ErrorRecord();
            errorRecord.setObjectName(getRecordType().getCode());
            if (dataSnapshot != null && dataSnapshot.length() > 2000) {
                dataSnapshot = dataSnapshot.substring(0, 2000) + "...(truncated)";
            }
//            errorRecord.setDataSnapshot(dataSnapshot);
//            errorRecord.setErrorMessage(ex.getMessage());
//            String stack = ExceptionUtils.getStackTrace(ex);
//            if (stack.length() > 2000) stack = stack.substring(0, 2000) + "...(truncated)";
//            errorRecord.setStackTrace(stack);
//            errorRecord.setRetryTimes(0); // 可在需要时从实体获取
            errorRecord.setErrorDesc(dataSnapshot);
            errorRecord.setErrorTime(DateUtils.getCurrentTimestamp());
            errorRecordService.saveErrorRecord(errorRecord);
        } catch (Exception e) {
            log.error("保存 ErrorRecord 失败", e);
        }
    }

    // ----- 监控 -----
    public void printMetrics() {
        long retrySize = 0;
        try { retrySize = RedisUtils.getListSize(retryListKey); } catch (Exception ignored) {}
        log.info("{} 监控 -> 提交:{} 成功:{} 失败:{} 丢弃:{} 队列深度:{} 重试队列:{}",
                getRecordType(),
                submittedCount.get(), successCount.get(), failedCount.get(), droppedCount.get(),
                batchInsertQueue.size(), retrySize);
    }

    // ----- 优雅停机 -----
    @PreDestroy
    public final void shutdown() {
        shuttingDown.set(true);
        log.info("{} 开始停机...", getRecordType());

        int attempts = 10;
        while (!batchInsertQueue.isEmpty() && attempts-- > 0) {
            doBatchInsert();
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        if (!batchInsertQueue.isEmpty()) {
            log.warn("{} 队列仍有数据，写入溢出文件", getRecordType());
            List<T> remaining = new ArrayList<>();
            batchInsertQueue.drainTo(remaining);
            for (T e : remaining) writeToOverflowFile(JSON.toJSONString(e));
        }

        synchronized (overflowLock) {
            if (overflowWriter != null) {
                try { overflowWriter.close(); } catch (IOException ignored) {}
            }
        }
        synchronized (archiveLock) {
            if (archiveWriter != null) {
                try { archiveWriter.close(); } catch (IOException ignored) {}
            }
        }
        log.info("{} 停机完成", getRecordType());
    }
}
