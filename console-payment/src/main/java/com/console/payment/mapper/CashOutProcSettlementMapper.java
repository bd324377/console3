package com.console.payment.mapper;

import com.console.payment.entity.CashOutProc;
import org.apache.ibatis.annotations.Param;

public interface CashOutProcSettlementMapper {
    CashOutProc selectNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);
    void insertOrUpdateCashOutReport(@Param("fromId") Long fromId, @Param("toId") Long toId);
}
