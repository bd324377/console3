package com.console.user.task;

import com.console.core.entity.Tenant;
import com.console.core.handler.ScheduledTask;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.user.entity.BindPhoneRecord;
import com.console.user.mapper.BindPhoneRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class PhoneSyncTask extends ScheduledTask {//子站点向总租户同步手机号
    @Resource
    private BindPhoneRecordMapper bindPhoneRecordMapper;

    @Override
    @Scheduled(cron = "${scheduled.dataPush.cron.phone}")
    public void execute() {
        if (!this.enabledDataPush) return;
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_PHONE_SYNC_PUSH);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) return;
            List<Tenant> tenantList = tenantService.getTenantList();
            if (!ObjectUtils.isEmpty(tenantList)) {
                for (Tenant tenant : tenantList) {
                    if (tenant.getId() <= 0) continue;
                    try {
                        settlementExecutor.execute(() -> {
                            try {
                                phoneSyncPush(tenant.getId());
                            } catch (Exception e) {
                                log.error("手机号同步任务失败, tenantId={}", tenant.getId(), e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.error("手机号同步任务提交被拒绝, tenantId={}", tenant.getId(), e);//线程队列已满，记入到错误表
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.info("等待手机号同步任务全局锁被中断。。。。");
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放手机号同步任务分布式锁失败");
                }
            }
        }
    }

    public void phoneSyncPush(Integer tenantId) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_PHONE_SYNC_PUSH + ":" + tenantId);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) {
                log.info("租户手机号同步任务已在进行，跳过本轮, tenantId={}", tenantId);
                return;
            }
            DynamicTableNameHandler.setTenantId(tenantId);

            for (int round = 0; round < SETTLEMENT_MAX_ROUNDS; round ++) {//最多循环三轮，防止大数据租户长时间占用线程
                Optional<BindPhoneRecord> result;
                try {
                    result = transactionTemplate.execute(status -> Optional.ofNullable(doSettleBatch(tenantId)));
                } catch (PessimisticLockingFailureException ex) {
                    log.warn("手机号同步任务游标行被占用，跳过本轮, tenantId={}", tenantId);// FOR UPDATE NOWAIT 失败：另一个事务正在处理本租户，放弃本轮
                    break;
                } catch (Exception e) {
                    log.error("手机号同步任务批次失败, tenantId={}", tenantId, e);
                    break;
                }
                if (result.isEmpty()) break;
                BindPhoneRecord bindPhoneRecord = result.get();
                if (bindPhoneRecord.getBindCount() == null || bindPhoneRecord.getBindCount() < SETTLEMENT_BATCH_SIZE) break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待租户手机号同步任务锁被中断。。。。, tenantId={}", tenantId);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放租户手机号同步任务锁失败, tenantId={}", tenantId, e);
                }
            }
            DynamicTableNameHandler.removeTenantId();
        }
    }

    private BindPhoneRecord doSettleBatch(Integer tenantId) {
        long start = System.currentTimeMillis();

        BindPhoneRecord bindPhoneRecord = bindPhoneRecordMapper.selectNewestRecord(SETTLEMENT_BATCH_SIZE);
        if (bindPhoneRecord == null || bindPhoneRecord.getId() == null) return null;
        Long toId = bindPhoneRecord.getId();

        bindPhoneRecordMapper.syncUserPhone(toId);
        bindPhoneRecordMapper.createRealUser(toId);
        bindPhoneRecordMapper.delSyncedRecords(toId);

        log.info("订手机号同步任务批次完成, tenantId={}, toId= {}, count={}, costMs={}",
                tenantId, toId, bindPhoneRecord.getBindTime(),System.currentTimeMillis() - start);
        return bindPhoneRecord;
    }
}
