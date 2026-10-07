//package com.console.core.merger;
//
//import com.console.core.entity.UserProc;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class RecordMerger {
//    @Resource
//    private UserProcMerger userProcMerger;
//
//    public void submitRecords(Object... records) {
//        for (Object record : records) {
//            if (record instanceof UserProc userProc) {
//                userProcMerger.submit(userProc);
//            }
//        }
//    }
//}
