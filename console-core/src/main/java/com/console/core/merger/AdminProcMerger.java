//package com.console.base.merger;
//
//import com.console.base.entity.UserProc;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class AdminProcMerger extends AbstractBatchMerger<UserProc> {
//    @Override
//    protected int getRetryTimes(UserProc entity) {
//        return 0;
//    }
//
//    @Override
//    protected RecordType getRecordType() {
//        return RecordType.;
//    }
//
//    @Override
//    protected Class<UserProc> getRecordClass() {
//        return UserProc.class;
//    }
//
//    @Override
//    protected void saveBatchIgnore(List<UserProc> objectList) {
//
//    }
//
//    @Override
//    protected void incrementRetryTimes(UserProc entity) {
//
//    }
//
//    @Override
//    protected boolean isFromRecovery(UserProc entity) {
//        return false;
//    }
//
//    @Override
//    protected void setFromRecovery(UserProc entity, boolean fromRecovery) {
//
//    }
//
//    @Override
//    protected String getBusinessKey(UserProc entity) {
//        return null;
//    }
//
//    @Override
//    protected Map<String, List<UserProc>> getGroupMap(List<UserProc> records) {
//        return null;
//    }
//}
