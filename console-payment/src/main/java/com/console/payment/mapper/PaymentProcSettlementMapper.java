package com.console.payment.mapper;

import com.console.payment.entity.PaymentProc;
import org.apache.ibatis.annotations.Param;

public interface PaymentProcSettlementMapper {
    PaymentProc selectNewestProc(@Param("settledProcId") Long settledProcId,@Param("limitSize") Integer limitSize);
    void insertOrUpdatePaymentReport(@Param("fromId") Long fromId, @Param("toId") Long toId);
}
