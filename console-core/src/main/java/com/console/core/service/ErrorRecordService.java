package com.console.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.ErrorRecord;

public interface ErrorRecordService extends IService<ErrorRecord> {
    void saveErrorRecord(ErrorRecord errorRecord);
}
