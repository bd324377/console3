package com.console.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.payment.entity.PaymentReport;

public interface PaymentReportMapper extends BaseMapper<PaymentReport> {
    @org.apache.ibatis.annotations.Delete("DELETE FROM s_payment_report WHERE report_date=#{date}")
    int deleteDate(java.time.LocalDate date);
    int rebuild(java.time.LocalDate date);
}
