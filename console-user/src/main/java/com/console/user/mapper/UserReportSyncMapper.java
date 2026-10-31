package com.console.user.mapper;

import com.console.core.entity.UserProc;
import org.apache.ibatis.annotations.Param;

public interface UserReportSyncMapper {
    UserProc selectNewestUserProc(@Param("syncedId") Long syncedId,@Param("limitSize") Integer limitSize);

    void syndUserProcReport(@Param("fromId") Long fromId,@Param("toId") Long toId);
}
