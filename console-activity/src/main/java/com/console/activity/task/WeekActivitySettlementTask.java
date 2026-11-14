package com.console.activity.task;

import com.console.core.handler.ScheduledTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WeekActivitySettlementTask extends ScheduledTask {
    @Override
    @Scheduled(cron = "${scheduled.settlement.cron.sysActivity:0 2 0 ? * MON}")
    public void execute() {

    }
}
