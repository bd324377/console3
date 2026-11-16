package com.console.activity.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.activity.entity.ActivityProc;
import com.console.activity.mapper.ActivityProcSettlementMapper;
import com.console.core.entity.SettledIdCursor;
import com.console.core.entity.Tenant;
import com.console.core.handler.ScheduledTask;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
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
public class ActivityProcSettlementTask extends ScheduledTask {
    @Resource
    private ActivityProcSettlementMapper activityProcSettlementMapper;
    @Override
    @Scheduled(cron = "${scheduled.settlement.cron.activityProc:5/50 * * * * ?}")
    public void execute() {
        if (!this.enabledSettlement) return;
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_ACTIVITY_PROC_SETTLEMENT);
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
                                settleActivityProc(tenant.getId());
                            } catch (Exception e) {
                                log.error("活动行为结算与汇总失败, tenantId={}", tenant.getId(), e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.error("活动行为结算任务提交被拒绝, tenantId={}", tenant.getId(), e);//线程队列已满，记入到错误表
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.info("等待活动行为结算全局锁被中断。。。。");
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放活动行为结算分布式锁失败");
                }
            }
        }
    }

    public void settleActivityProc(Integer tenantId) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_ACTIVITY_PROC_SETTLEMENT + ":" + tenantId);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) {
                log.info("租户活动行为结算已在进行，跳过本轮, tenantId={}", tenantId);
                return;
            }
            DynamicTableNameHandler.setTenantId(tenantId);
            for (int round = 0; round < SETTLEMENT_MAX_ROUNDS; round ++) {//最多循环三轮，防止大数据租户长时间占用线程
                Optional<ActivityProc> result;
                try {
                    result = transactionTemplate.execute(status -> Optional.ofNullable(doSettleBatch(tenantId)));
                } catch (PessimisticLockingFailureException ex) {
                    log.warn("活动行为结算游标行被占用，跳过本轮, tenantId={}", tenantId);// FOR UPDATE NOWAIT 失败：另一个事务正在处理本租户，放弃本轮
                    break;
                } catch (Exception e) {
                    log.error("活动行为结算批次失败, tenantId={}", tenantId, e);
                    break;
                }
                if (result.isEmpty()) break;
                ActivityProc activityProc = result.get();
                if (activityProc.getProcCount() == null || activityProc.getProcCount() < SETTLEMENT_BATCH_SIZE) break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待租户活动行为结算锁被中断。。。。, tenantId={}", tenantId);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放租户结算锁失败, tenantId={}", tenantId, e);
                }
            }
            DynamicTableNameHandler.removeTenantId();
        }
    }

    private ActivityProc doSettleBatch(Integer tenantId) {
        long start = System.currentTimeMillis();
        SettledIdCursor settledIdCursor = getSettledIdCursor(SettledIdCursor.SettlementType.ACTIVITY_PROC);
        if (settledIdCursor == null) {
            log.warn("结算游标未初始化, tenantId={}", tenantId);
            initSettledIdCursor(SettledIdCursor.SettlementType.ACTIVITY_PROC);//初始化游标
            settledIdCursor = new SettledIdCursor();
            settledIdCursor.setSettlementType(SettledIdCursor.SettlementType.ACTIVITY_PROC);
            settledIdCursor.setSettledId(0L);
        }
        long fromId = settledIdCursor.getSettledId();//游标
        ActivityProc activityProc = activityProcSettlementMapper.selectNewestActivityProc(fromId,SETTLEMENT_BATCH_SIZE);
        if (activityProc == null || activityProc.getId() == null) return null;
        Long toId = activityProc.getId();
        if (toId <= fromId) {
            log.error("活动行为结算游标未前进，tenantId={}, fromId={}, toId={}", tenantId, fromId, toId);
            return null;
        }
        activityProcSettlementMapper.insertOrUpdateActivityReport(fromId + 1, toId);
        //更新业务表最大活动过程结算ID
        UpdateWrapper<SettledIdCursor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SettledIdCursor::getSettlementType,SettledIdCursor.SettlementType.ACTIVITY_PROC)
                .eq(SettledIdCursor::getSettledId,fromId)
                .set(SettledIdCursor::getSettledId,toId);
        int rows = updateSettledIdCursor(updateWrapper);
        if (rows == 0) {//添加乐观锁双保险
            throw new IllegalStateException("活动行为游标并发修改，回滚本批, tenantId=" + tenantId + ", fromId=" + fromId + ", toId=" + toId);
        }
        log.info("活动行为结算批次完成, tenantId={}, range=({}, {}], count={}, costMs={}",
                tenantId, fromId, toId, activityProc.getProcCount(),System.currentTimeMillis() - start);
        return activityProc;
    }
}
