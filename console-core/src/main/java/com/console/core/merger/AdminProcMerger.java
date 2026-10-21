package com.console.core.merger;

import com.console.core.entity.AdminProc;
import com.console.core.mapper.AdminProcMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AdminProcMerger extends AbstractBatchMerger<AdminProc> {
    @Resource
    private AdminProcMapper adminProcMapper;
    @Override
    protected int getRetryTimes(AdminProc entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.ADMIN_PROC;
    }

    @Override
    protected Class<AdminProc> getRecordClass() {
        return AdminProc.class;
    }

    @Override
    protected void saveBatchIgnore(List<AdminProc> objectList) {
        adminProcMapper.insert(objectList);
    }

    @Override
    protected void incrementRetryTimes(AdminProc entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(AdminProc entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(AdminProc entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected Serializable getBusinessKey(AdminProc entity) {
        return entity.getId();
    }

    @Override
    protected Map<Integer, List<AdminProc>> getGroupMap(List<AdminProc> records) {
        return records.stream().collect(Collectors.groupingBy(AdminProc::getTenantId));
    }
}
