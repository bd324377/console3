package com.console.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.user.entity.UserReportPeriodAgg;

import java.time.LocalDate;

public interface UserReportPeriodAggMapper extends BaseMapper<UserReportPeriodAgg> {
    void rebuildUserPeriodReport(Integer periodType, LocalDate startDate, LocalDate endDate);
}
