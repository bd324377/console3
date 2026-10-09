package com.console.payment.merger;

import com.console.core.merger.AbstractBatchMerger;
import com.console.core.merger.RecordType;
import com.console.payment.entity.CashOutProc;
import com.console.payment.mapper.CashOutProcMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CashOutProcMerger extends AbstractBatchMerger<CashOutProc> {
    @Resource
    private CashOutProcMapper cashOutProcMapper;
    @Override
    protected int getRetryTimes(CashOutProc entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.CASH_OUT_PROC;
    }

    @Override
    protected Class<CashOutProc> getRecordClass() {
        return CashOutProc.class;
    }

    @Override
    protected void saveBatchIgnore(List<CashOutProc> objectList) {
        cashOutProcMapper.insert(objectList);
    }

    @Override
    protected void incrementRetryTimes(CashOutProc entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(CashOutProc entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(CashOutProc entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected String getBusinessKey(CashOutProc entity) {
        return entity.getId().toString();
    }

    @Override
    protected Map<Integer, List<CashOutProc>> getGroupMap(List<CashOutProc> records) {
        return records.stream().collect(Collectors.groupingBy(CashOutProc::getTenantId));
    }
}
