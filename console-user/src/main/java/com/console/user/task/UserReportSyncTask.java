package com.console.user.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.entity.SettledIdCursor;
import com.console.core.entity.Tenant;
import com.console.core.entity.UserProc;
import com.console.core.handler.ScheduledTask;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.user.mapper.UserReportSyncMapper;
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
public class UserReportSyncTask extends ScheduledTask {
    @Resource
    private UserReportSyncMapper userReportSyncMapper;

    /** 用户日汇总表同步 **/
    @Override
    @Scheduled(cron = "${scheduled.dataPush.userReport}")
    public void execute() {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_USER_REPORT_SYNC_PUSH);
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
                                userProcSyncPush(tenant.getId());
                            } catch (Exception e) {
                                log.error("同步用户行为报表失败, tenantId={}", tenant.getId(), e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.error("同步用户行为报表任务提交被拒绝, tenantId={}", tenant.getId(), e);//线程队列已满，记入到错误表
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.info("等待订同步用户行为报表全局锁被中断。。。。");
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放同步用户行为报表分布式锁失败");
                }
            }
        }
    }

    public void userProcSyncPush(Integer tenantId) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_USER_REPORT_SYNC_PUSH + ":" + tenantId);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) {
                log.info("租户同步用户行为报表已在进行，跳过本轮, tenantId={}", tenantId);
                return;
            }
            DynamicTableNameHandler.setTenantId(tenantId);
            for (int round = 0; round < SETTLEMENT_MAX_ROUNDS; round ++) {//最多循环三轮，防止大数据租户长时间占用线程
                Optional<UserProc> result;
                try {
                    result = transactionTemplate.execute(status -> Optional.ofNullable(doSyncBatch(tenantId)));
                } catch (PessimisticLockingFailureException ex) {
                    log.warn("同步用户行为报表游标行被占用，跳过本轮, tenantId={}", tenantId);// FOR UPDATE NOWAIT 失败：另一个事务正在处理本租户，放弃本轮
                    break;
                } catch (Exception e) {
                    log.error("同步用户行为报表批次失败, tenantId={}", tenantId, e);
                    break;
                }
                if (result.isEmpty()) break;
                UserProc userProc = result.get();
                if (userProc.getProcCount() == null || userProc.getProcCount() < SETTLEMENT_BATCH_SIZE) break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待租户同步用户行为报表锁被中断。。。。, tenantId={}", tenantId);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放租户同步用户行为报表锁失败, tenantId={}", tenantId, e);
                }
            }
            DynamicTableNameHandler.removeTenantId();
        }
    }

    private UserProc doSyncBatch(Integer tenantId) {
        long start = System.currentTimeMillis();
        SettledIdCursor settledIdCursor = getSettledIdCursor(SettledIdCursor.SettlementType.USER_PROC_SYNC);
        if (settledIdCursor == null) {
            log.warn("同步用户行为报表游标未初始化, tenantId={}", tenantId);
            initSettledIdCursor(SettledIdCursor.SettlementType.USER_PROC_SYNC);//初始化游标
            settledIdCursor = new SettledIdCursor();
            settledIdCursor.setSettlementType(SettledIdCursor.SettlementType.USER_PROC_SYNC);
            settledIdCursor.setSettledId(0L);
        }
        long fromId = settledIdCursor.getSettledId();//游标
        UserProc userProc = userReportSyncMapper.selectNewestUserProc(fromId,SETTLEMENT_BATCH_SIZE);
        if (userProc == null || userProc.getId() == null) return null;
        Long toId = userProc.getId();
        if (toId <= fromId) {
            log.error("同步用户行为报表游标未前进，tenantId={}, fromId={}, toId={}", tenantId, fromId, toId);
            return null;
        }
        userReportSyncMapper.syndUserProcReport(fromId + 1, toId);
        //更新业务表最大活动过程结算ID
        UpdateWrapper<SettledIdCursor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SettledIdCursor::getSettlementType,SettledIdCursor.SettlementType.USER_PROC_SYNC)
                .eq(SettledIdCursor::getSettledId,fromId)
                .set(SettledIdCursor::getSettledId,toId);
        int rows = updateSettledIdCursor(updateWrapper);
        if (rows == 0) {//添加乐观锁双保险
            throw new IllegalStateException(
                    "同步用户行为报表游标并发修改，回滚本批, tenantId=" + tenantId + ", fromId=" + fromId + ", toId=" + toId);
        }
        log.info("同步用户行为报表批次完成, tenantId={}, range=({}, {}], count={}, costMs={}",
                tenantId, fromId, toId, userProc.getProcCount(),System.currentTimeMillis() - start);
        return userProc;
    }
}
