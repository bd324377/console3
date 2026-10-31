package com.console.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.user.entity.BindPhoneRecord;
import org.apache.ibatis.annotations.Param;

public interface BindPhoneRecordMapper extends BaseMapper<BindPhoneRecord> {
    BindPhoneRecord selectNewestRecord(@Param("limitSize") Integer limitSize);
    void syncUserPhone(@Param("toId") Long toId);
    void createRealUser(@Param("toId") Long toId);
    void delSyncedRecords(@Param("toId") Long toId);
}
