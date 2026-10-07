package com.console.core.service.impl;

import com.console.core.entity.ErrorRecord;
import com.console.core.mapper.ErrorRecordMapper;
import com.console.core.service.ErrorRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ErrorRecordServiceImpl extends BaseServiceImpl<ErrorRecordMapper, ErrorRecord> implements ErrorRecordService {
    @Async("errorRecordExecutor")
    @Override
    public void saveErrorRecord(ErrorRecord errorRecord) {
        try {
            this.save(errorRecord);
        } catch (Exception e) {
            log.error("保存异常记录失败: {}", e.getMessage(), e);
        }
    }
}
