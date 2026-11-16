package com.console.admin.controller;

import com.console.admin.service.DashboardService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Resource
    private DashboardService dashboardService;

    @GetMapping("/todayReport")
    public Result getTodayReport() {
        return Result.success(dashboardService.getTodayReport());
    }

    @GetMapping("/timePeriodReportList")
    public Result getTimePeriodReportList() {
        return Result.success(dashboardService.getTodayReport());
    }
}
