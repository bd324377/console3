package com.console.user.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.entity.SettledIdCursor;
import com.console.core.entity.Tenant;
import com.console.core.handler.ScheduledTask;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.user.entity.AgentLayer;
import com.console.user.mapper.AgentLayerMapper;
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
public class AgentLayerSyncTask extends ScheduledTask {
    @Resource
    private AgentLayerMapper agentLayerMapper;

    /** 代理层级关系同步 **/
    @Override
    @Scheduled(cron = "${scheduled.dataPush.cron.agentLayer}")
    public void execute() {
        if (!this.enabledDataPush) return;
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_AGENT_LAYER_SYNC_PUSH);
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
                                agentLayerSyncPush(tenant.getId());
                            } catch (Exception e) {
                                log.error("订单结算失败, tenantId={}", tenant.getId(), e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.error("订单结算任务提交被拒绝, tenantId={}", tenant.getId(), e);//线程队列已满，记入到错误表
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.info("等待订单结算全局锁被中断。。。。");
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放订单结算分布式锁失败");
                }
            }
        }
    }

    public void agentLayerSyncPush(Integer tenantId) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_AGENT_LAYER_SYNC_PUSH + ":" + tenantId);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) {
                log.info("租户订单结算已在进行，跳过本轮, tenantId={}", tenantId);
                return;
            }
            DynamicTableNameHandler.setTenantId(tenantId);

            for (int round = 0; round < SETTLEMENT_MAX_ROUNDS; round ++) {//最多循环三轮，防止大数据租户长时间占用线程
                Optional<AgentLayer> result;
                try {
                    result = transactionTemplate.execute(status -> Optional.ofNullable(doSyncBatch(tenantId)));
                } catch (PessimisticLockingFailureException ex) {
                    log.warn("结算游标行被占用，跳过本轮, tenantId={}", tenantId);// FOR UPDATE NOWAIT 失败：另一个事务正在处理本租户，放弃本轮
                    break;
                } catch (Exception e) {
                    log.error("订单结算批次失败, tenantId={}", tenantId, e);
                    break;
                }
                if (result.isEmpty()) break;
                AgentLayer agentLayer = result.get();
                if (agentLayer.getAgentLayerCount() == null || agentLayer.getAgentLayerCount() < SETTLEMENT_BATCH_SIZE) break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待租户同步用户上下级关系锁被中断。。。。, tenantId={}", tenantId);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放租户同步用户上下级关系锁失败, tenantId={}", tenantId, e);
                }
            }
            DynamicTableNameHandler.removeTenantId();
        }
    }

    private AgentLayer doSyncBatch(Integer tenantId) {
        long start = System.currentTimeMillis();
        SettledIdCursor settledIdCursor = getSettledIdCursor(SettledIdCursor.SettlementType.AGENT_LAYER_SYNC);
        if (settledIdCursor == null) {
            log.warn("同步用户上下级关系游标未初始化, tenantId={}", tenantId);
            initSettledIdCursor(SettledIdCursor.SettlementType.AGENT_LAYER_SYNC);//初始化游标
            settledIdCursor = new SettledIdCursor();
            settledIdCursor.setSettlementType(SettledIdCursor.SettlementType.AGENT_LAYER_SYNC);
            settledIdCursor.setSettledId(0L);
        }
        long fromId = settledIdCursor.getSettledId();//游标
        AgentLayer agentLayer = agentLayerMapper.selectNewestRecord(fromId,SETTLEMENT_BATCH_SIZE);
        if (agentLayer == null || agentLayer.getId() == null) return null;
        Long toId = agentLayer.getId();
        if (toId <= fromId) {
            log.error("同步用户上下级关系游标未前进，tenantId={}, fromId={}, toId={}", tenantId, fromId, toId);
            return null;
        }
        agentLayerMapper.syncUserLayer(fromId + 1, toId);
        //更新业务表最大活动过程结算ID
        UpdateWrapper<SettledIdCursor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SettledIdCursor::getSettlementType,SettledIdCursor.SettlementType.AGENT_LAYER_SYNC)
                .eq(SettledIdCursor::getSettledId,fromId)
                .set(SettledIdCursor::getSettledId,toId);
        int rows = updateSettledIdCursor(updateWrapper);
        if (rows == 0) {//添加乐观锁双保险
            throw new IllegalStateException(
                    "同步用户上下级关系游标并发修改，回滚本批, tenantId=" + tenantId + ", fromId=" + fromId + ", toId=" + toId);
        }
        log.info("同步用户上下级关系批次完成, tenantId={}, range=({}, {}], count={}, costMs={}",
                tenantId, fromId, toId, agentLayer.getAgentLayerCount(),System.currentTimeMillis() - start);
        return agentLayer;
    }
}
