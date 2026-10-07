package com.console.user.merger;
import com.console.core.merger.AbstractBatchMerger;
import com.console.core.merger.RecordType;
import com.console.user.entity.UserId;
import com.console.user.mapper.UserIdMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserIdMerger extends AbstractBatchMerger<UserId> {
    @Resource
    private UserIdMapper userIdMapper;
    @Override
    protected int getRetryTimes(UserId entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.USER_ID;
    }

    @Override
    protected Class<UserId> getRecordClass() {
        return UserId.class;
    }

    @Override
    protected void saveBatchIgnore(List<UserId> objectList) {
        userIdMapper.insert(objectList);
    }

    @Override
    protected void incrementRetryTimes(UserId entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(UserId entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(UserId entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected String getBusinessKey(UserId entity) {
        return entity.getUserId().toString();
    }

    @Override
    protected Map<Integer, List<UserId>> getGroupMap(List<UserId> records) {
        Map<Integer, List<UserId>> groupMap = new HashMap<>();
        groupMap.put(0,records);
        return groupMap;
    }
}
