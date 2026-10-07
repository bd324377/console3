package com.console.core.merger;

import com.console.core.entity.UserProc;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserProcMerger extends AbstractBatchMerger<UserProc> {

    @Override
    protected int getRetryTimes(UserProc entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.USER_PROC;
    }

    @Override
    protected Class<UserProc> getRecordClass() {
        return UserProc.class;
    }

    @Override
    protected void saveBatchIgnore(List<UserProc> objectList) {

    }

    @Override
    protected void incrementRetryTimes(UserProc userProc) {
        userProc.setRetryTimes(userProc.getRetryTimes() == null ? 0 : (userProc.getRetryTimes() + 1));
    }
    @Override
    protected boolean isFromRecovery(UserProc userProc) {
        return userProc.getFromRecovery() != null && userProc.getFromRecovery();
    }
    @Override
    protected void setFromRecovery(UserProc userProc, boolean fromRecovery) {
        userProc.setFromRecovery(fromRecovery);
    }
    @Override
    protected String getBusinessKey(UserProc userProc) {
        return userProc.getUserId().toString();
    }

    @Override
    protected Map<Integer, List<UserProc>> getGroupMap(List<UserProc> records) {
        return records.stream().collect(Collectors.groupingBy(UserProc::getTenantId));
    }
}
