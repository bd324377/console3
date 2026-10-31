package com.console.payment.mapper;

import com.console.core.entity.WalletProc;
import org.apache.ibatis.annotations.Param;

public interface WalletProcSettlementMapper {
    WalletProc selectNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);
    void insertOrUpdateWalletReport(@Param("fromId") Long fromId, @Param("toId") Long toId);
    void updateSysBalanceReport();
}
