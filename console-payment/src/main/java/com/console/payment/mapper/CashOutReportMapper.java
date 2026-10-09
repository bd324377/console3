package com.console.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.payment.entity.CashOutReport;

public interface CashOutReportMapper extends BaseMapper<CashOutReport> {
    @org.apache.ibatis.annotations.Delete("DELETE FROM s_cash_out_report WHERE report_date=#{date}")
    int deleteDate(java.time.LocalDate date);
    int rebuild(java.time.LocalDate date);
}
