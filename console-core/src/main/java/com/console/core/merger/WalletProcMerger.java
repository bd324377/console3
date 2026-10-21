package com.console.core.merger;

import com.console.core.entity.WalletProc;
import com.console.core.mapper.WalletProcMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WalletProcMerger extends AbstractBatchMerger<WalletProc> {
    @Resource
    private WalletProcMapper walletProcMapper;

    @Override
    protected int getRetryTimes(WalletProc entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.WALLET_PROC;
    }

    @Override
    protected Class<WalletProc> getRecordClass() {
        return WalletProc.class;
    }

    @Override
    protected void saveBatchIgnore(List<WalletProc> objectList) {
        walletProcMapper.insert(objectList);
    }

    @Override
    protected void incrementRetryTimes(WalletProc entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(WalletProc entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(WalletProc entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected Serializable getBusinessKey(WalletProc entity) {
        return entity.getId();
    }

    @Override
    protected Map<Integer, List<WalletProc>> getGroupMap(List<WalletProc> records) {
        return records.stream().collect(Collectors.groupingBy(WalletProc::getTenantId));
    }
}
