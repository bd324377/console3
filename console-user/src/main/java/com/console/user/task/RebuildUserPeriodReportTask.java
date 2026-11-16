package com.console.user.task;

import com.console.framework.utils.DateUtils;
import com.console.user.mapper.UserReportPeriodAggMapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RebuildUserPeriodReportTask {//1今日 2昨日 3本周 4上周 5本月 6上月
    @Resource
    private UserReportPeriodAggMapper userReportPeriodAggMapper;

    @Scheduled(cron = "0 5 0 * * *")
    public void rebuildFrozenPeriods() {//   昨日/上周/上月 数据刷新，每日凌晨刷新
        userReportPeriodAggMapper.rebuildUserPeriodReport(2, DateUtils.yesterday(), DateUtils.yesterday());
        userReportPeriodAggMapper.rebuildUserPeriodReport(4, DateUtils.lastWeekMonday(), DateUtils.lastWeekMonday());
        userReportPeriodAggMapper.rebuildUserPeriodReport(6, DateUtils.lastMonthFirstDate(), DateUtils.lastMonthLastDate());
    }

    @Scheduled(cron = "1 1/5 * * * ?")
    public void refreshActivePeriods() {//   今日/本周/本月 数据刷新，每隔五分钟刷新
        userReportPeriodAggMapper.rebuildUserPeriodReport(1, LocalDate.now(), LocalDate.now());
        userReportPeriodAggMapper.rebuildUserPeriodReport(3, DateUtils.thisWeekFirstDate(), DateUtils.thisWeekLastDate());
        userReportPeriodAggMapper.rebuildUserPeriodReport(4, DateUtils.thisMonthFirstDate(), DateUtils.thisMonthLastDate());
    }
}
