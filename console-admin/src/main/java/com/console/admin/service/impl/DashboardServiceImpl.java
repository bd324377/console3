package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.admin.service.DashboardService;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.RequestUtils;
import com.console.user.entity.UserReportPeriodAgg;
import com.console.user.service.UserReportPeriodAggService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Resource
    private UserReportPeriodAggService userReportPeriodAggService;

    @Override
    public UserReportPeriodAgg getTodayReport() {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<UserReportPeriodAgg> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserReportPeriodAgg::getPeriodType,1);
        return userReportPeriodAggService.getOne(queryWrapper);
    }

    @Override
    public List<UserReportPeriodAgg> getTimePeriodReportList() {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<UserReportPeriodAgg> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().groupBy(UserReportPeriodAgg::getPeriodType);
        return userReportPeriodAggService.list(queryWrapper);
    }


}
