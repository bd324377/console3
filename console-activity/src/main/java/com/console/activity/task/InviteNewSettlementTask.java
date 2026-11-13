package com.console.activity.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.activity.mapper.InviteNewSettlementMapper;
import com.console.activity.task.handler.inviteNew.model.InviteNewSettlementParam;
import com.console.core.entity.SettledIdCursor;
import com.console.core.entity.Tenant;
import com.console.core.entity.UserProc;
import com.console.core.handler.ScheduledTask;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.ActivityEvent;
import com.console.framework.constants.ActivityType;
import com.console.framework.constants.CacheConstants;
import com.console.framework.utils.SpringUtils;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
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
public class InviteNewSettlementTask extends ScheduledTask {
    @Resource
    private InviteNewSettlementMapper inviteNewSettlementMapper;
    @Override
    @Scheduled(cron = "${scheduled.settlement.cron.walletProc}")
    public void execute() {
        if (!this.enabledSettlement) return;
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_INVITE_NEW_USER_SETTLEMENT);
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
                                inviteNewSettle(tenant.getId());
                            } catch (Exception e) {
                                log.error("邀新宝箱结算失败, tenantId={}", tenant.getId(), e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.error("邀新宝箱结算任务提交被拒绝, tenantId={}", tenant.getId(), e);//线程队列已满，记入到错误表
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.info("等待邀新宝箱结算全局锁被中断。。。。");
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放邀新宝箱结算分布式锁失败");
                }
            }
        }
    }

    public void inviteNewSettle(Integer tenantId) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_INVITE_NEW_USER_SETTLEMENT + ":" + tenantId);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) {
                log.info("租户邀新宝箱结算已在进行，跳过本轮, tenantId={}", tenantId);
                return;
            }
            DynamicTableNameHandler.setTenantId(tenantId);
            for (int round = 0; round < SETTLEMENT_MAX_ROUNDS; round ++) {//最多循环三轮，防止大数据租户长时间占用线程
                Optional<InviteNewSettlementResult> result;
                try {
                    result = transactionTemplate.execute(status -> Optional.ofNullable(doSettleBatch(tenantId)));
                } catch (PessimisticLockingFailureException ex) {
                    log.warn("邀新宝箱结算汇总游标行被占用，跳过本轮, tenantId={}", tenantId);// FOR UPDATE NOWAIT 失败：另一个事务正在处理本租户，放弃本轮
                    break;
                } catch (Exception e) {
                    log.error("邀新宝箱结算汇总批次失败, tenantId={}", tenantId, e);
                    break;
                }
                if (result.isEmpty()) break;
                InviteNewSettlementResult inviteNewSettlementResult = result.get();
                if (!ObjectUtils.isEmpty(inviteNewSettlementResult.getInviteNewSettlementParams())) {
                    for (InviteNewSettlementParam inviteNewSettlementParam : inviteNewSettlementResult.getInviteNewSettlementParams()) {
                        ActivityEvent activityEvent = new ActivityEvent(ActivityType.INVITE_NEW_USERS,tenantId,inviteNewSettlementParam);
                        SpringUtils.publishEvent(activityEvent);
                    }
                }
                if (inviteNewSettlementResult.getUserProc().getProcCount() == null || inviteNewSettlementResult.getUserProc().getProcCount() < SETTLEMENT_BATCH_SIZE) break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待租户邀新宝箱结算锁被中断。。。。, tenantId={}", tenantId);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放租户邀新宝箱结算锁失败, tenantId={}", tenantId, e);
                }
            }
            DynamicTableNameHandler.removeTenantId();
        }
    }

    private InviteNewSettlementResult doSettleBatch(Integer tenantId) {
        long start = System.currentTimeMillis();
        SettledIdCursor settledIdCursor = getSettledIdCursor(SettledIdCursor.SettlementType.INVITE_NEW);
        if (settledIdCursor == null) {
            log.warn("邀新宝箱结算游标未初始化, tenantId={}", tenantId);
            initSettledIdCursor(SettledIdCursor.SettlementType.INVITE_NEW);//初始化游标
            settledIdCursor = new SettledIdCursor();
            settledIdCursor.setSettlementType(SettledIdCursor.SettlementType.INVITE_NEW);
            settledIdCursor.setSettledId(0L);
        }
        long fromId = settledIdCursor.getSettledId();//游标
        UserProc userProc = inviteNewSettlementMapper.selectNewestProc(fromId,SETTLEMENT_BATCH_SIZE);
        if (userProc == null || userProc.getId() == null) return null;
        Long toId = userProc.getId();
        if (toId <= fromId) {
            log.error("邀新宝箱结算游标未前进，tenantId={}, fromId={}, toId={}", tenantId, fromId, toId);
            return null;
        }
        List<InviteNewSettlementParam> inviteNewSettlementParams = null;
        if (userProc.getActivateCount() != null && userProc.getActivateCount() > 0) {
            inviteNewSettlementParams = inviteNewSettlementMapper.getInviteNewSettlementParams(fromId + 1, toId);
        }
        //更新业务表最大活动过程结算ID
        UpdateWrapper<SettledIdCursor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SettledIdCursor::getSettlementType,SettledIdCursor.SettlementType.INVITE_NEW)
                .eq(SettledIdCursor::getSettledId,fromId)
                .set(SettledIdCursor::getSettledId,toId);
        int rows = updateSettledIdCursor(updateWrapper);
        if (rows == 0) {//添加乐观锁双保险
            throw new IllegalStateException(
                    "邀新宝箱结算游标并发修改，回滚本批, tenantId=" + tenantId + ", fromId=" + fromId + ", toId=" + toId);
        }
        log.info("邀新宝箱结算汇总批次完成, tenantId={}, range=({}, {}], count={}, costMs={}",
                tenantId, fromId, toId, userProc.getProcCount(),System.currentTimeMillis() - start);
        InviteNewSettlementResult inviteNewSettlementResult = new InviteNewSettlementResult();
        inviteNewSettlementResult.setUserProc(userProc);
        inviteNewSettlementResult.setInviteNewSettlementParams(inviteNewSettlementParams);
        return inviteNewSettlementResult;
    }

    @Getter
    @Setter
    private static class InviteNewSettlementResult {
        private UserProc userProc;
        private List<InviteNewSettlementParam> inviteNewSettlementParams;
    }
}
