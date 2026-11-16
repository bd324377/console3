package com.console.admin.service;

import com.console.user.entity.UserReportPeriodAgg;

import java.util.List;

public interface DashboardService {
    UserReportPeriodAgg getTodayReport();
    List<UserReportPeriodAgg> getTimePeriodReportList();
}
