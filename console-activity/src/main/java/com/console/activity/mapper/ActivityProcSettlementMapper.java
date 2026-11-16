package com.console.activity.mapper;

import com.console.activity.entity.ActivityProc;
import org.apache.ibatis.annotations.Param;

public interface ActivityProcSettlementMapper {
    ActivityProc selectNewestActivityProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);
    void insertOrUpdateActivityReport(@Param("fromId") Long fromId,@Param("toId") Long toId);
}
