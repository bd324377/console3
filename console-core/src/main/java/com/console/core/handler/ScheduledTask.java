package com.console.core.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.entity.SettledIdCursor;
import com.console.core.mapper.SettledIdCursorMapper;
import com.console.core.service.TenantService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Executor;

public abstract class ScheduledTask {
    @Value("${scheduled.settlement.enabled}")
    protected boolean enabledSettlement;
    @Value("${scheduled.dataPush.enabled}")
    protected boolean enabledDataPush;
    protected static final int SETTLEMENT_BATCH_SIZE = 10_000;//每次批量操作最大数量
    protected static final int SETTLEMENT_MAX_ROUNDS = 3;//循环批量操作最大轮数
    @Resource
    protected RedisLockRegistry lockRegistry;
    @Resource
    protected TenantService tenantService;
    @Resource
    protected SettledIdCursorMapper settledIdCursorMapper;
    // 线程池（由外部注入）
    @Resource(name = "settlementExecutor")
    protected Executor settlementExecutor;
    @Resource
    protected TransactionTemplate transactionTemplate;

    protected SettledIdCursor getSettledIdCursor(SettledIdCursor.SettlementType settlementType) {
        QueryWrapper<SettledIdCursor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SettledIdCursor::getSettlementType, settlementType).last(" FOR UPDATE NOWAIT");
        return settledIdCursorMapper.selectOne(queryWrapper);
    }

    protected int updateSettledIdCursor(UpdateWrapper<SettledIdCursor> updateWrapper) {
        return settledIdCursorMapper.update(updateWrapper);
    }

    protected void initSettledIdCursor(SettledIdCursor.SettlementType settlementType) {
        settledIdCursorMapper.initSettledIdCursor(settlementType.getKey());
    }

    public abstract void execute();
}
