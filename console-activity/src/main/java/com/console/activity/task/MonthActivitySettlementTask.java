package com.console.activity.task;

import com.console.core.handler.ScheduledTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonthActivitySettlementTask extends ScheduledTask {//月薪、月破产保护
    @Override
    @Scheduled(cron = "${scheduled.settlement.cron.sysActivity:0 3 0 1 * ?}")
    public void execute() {

    }
}
