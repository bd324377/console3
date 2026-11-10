package com.console.activity.merger;

import com.console.activity.entity.ActivityProc;
import com.console.activity.mapper.ActivityProcMapper;
import com.console.core.merger.AbstractBatchMerger;
import com.console.core.merger.RecordType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ActivityProcMerger extends AbstractBatchMerger<ActivityProc> {
    @Resource
    private ActivityProcMapper activityProcMapper;

    @Override
    protected int getRetryTimes(ActivityProc entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.ACTIVITY_PROC;
    }

    @Override
    protected Class<ActivityProc> getRecordClass() {
        return ActivityProc.class;
    }

    @Override
    protected void saveBatchIgnore(List<ActivityProc> objectList) {
        activityProcMapper.insert(objectList);
    }

    @Override
    protected void incrementRetryTimes(ActivityProc entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(ActivityProc entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(ActivityProc entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected Serializable getBusinessKey(ActivityProc entity) {
        return  entity.getId();
    }

    @Override
    protected Map<Integer, List<ActivityProc>> getGroupMap(List<ActivityProc> records) {
        return records.stream().collect(Collectors.groupingBy(ActivityProc::getTenantId));
    }
}
