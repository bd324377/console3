package com.console.user.task;

import com.console.core.entity.MarketSite;
import com.console.core.entity.Marketer;
import com.console.core.entity.Tenant;
import com.console.core.handler.ScheduledTask;
import com.console.core.service.MarketSiteService;
import com.console.core.service.MarketerService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.framework.utils.RedisUtils;
import com.console.framework.utils.TokenUtils;
import com.console.user.entity.OnlineReport;
import com.console.user.mapper.OnlineReportMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class OnlineUserSettlementTask extends ScheduledTask {
    @Resource
    private OnlineReportMapper onlineReportMapper;
    @Resource
    private MarketSiteService marketSiteService;
    @Resource
    private MarketerService marketerService;

    @Override
    @Scheduled(cron = "${scheduled.settlement.cron.onlineUser}")
    public void execute() {
        if (!this.enabledSettlement) return;
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_ONLINE_USER_SETTLEMENT);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) return;
            List<Tenant> tenantList = tenantService.getTenantList();
            if (!ObjectUtils.isEmpty(tenantList)) {
                for (Tenant tenant : tenantList) {
                    if (tenant.getId() <= 0) continue;
                    try {
                        settlementExecutor.execute(() -> {
                            try {
                                onlineUserSettlement(tenant.getId());
                            } catch (Exception e) {
                                log.error("在线人数统计任务失败, tenantId={}", tenant.getId(), e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.error("在线人数统计任务提交被拒绝, tenantId={}", tenant.getId(), e);//线程队列已满，记入到错误表
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.info("等待在线人数统计任务全局锁被中断。。。。");
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放在线人数统计任务分布式锁失败");
                }
            }
        }
    }

    public void onlineUserSettlement(Integer tenantId) {
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_ONLINE_USER_SETTLEMENT + ":" + tenantId);
        boolean locked = false;
        try {
            locked = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!locked) {
                log.info("在线用户统计任务已在进行，跳过本轮, tenantId={}", tenantId);
                return;
            }
            DynamicTableNameHandler.setTenantId(tenantId);
            List<MarketSite> marketSiteList = marketSiteService.getMarketSiteList(tenantId);
            LocalDate reportDate = LocalDate.now();
            LocalDateTime reportTime = LocalDateTime.now();
            List<OnlineReport> onlineReportList = new ArrayList<>();
            for (MarketSite marketSite : marketSiteList) {
                List<Marketer> marketerList = marketerService.getMarketerListBySiteId(marketSite.getId());
                for (Marketer marketer : marketerList) {
                    OnlineReport onlineReport = new OnlineReport();
                    onlineReport.setReportDate(reportDate);
                    onlineReport.setReportTime(reportTime);
                    onlineReport.setTenantId(marketer.getTenantId());
                    onlineReport.setMarketSiteId(marketer.getMarketSiteId());
                    onlineReport.setMarketTeamId(marketer.getMarketTeamId());
                    onlineReport.setMarketerId(marketer.getMarketerId());
                    onlineReport.setOnlineUserCount(TokenUtils.getAccessTokenCount(marketer.getTenantId(),marketer.getMarketSiteId(),marketer.getMarketerId()));
                    onlineReportList.add(onlineReport);
                    String onlineKey = CacheConstants.ONLINE_USER_COUNT + marketer.getTenantId() + ":" + marketer.getMarketSiteId() + ":" + marketer.getId();
                    RedisUtils.setValue(onlineKey,onlineReport.getOnlineUserCount());
                }
            }
            onlineReportMapper.insert(onlineReportList,1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待租户在线用户统计任务锁被中断。。。。, tenantId={}", tenantId);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.warn("释放租户在线用户统计任务锁失败, tenantId={}", tenantId, e);
                }
            }
            DynamicTableNameHandler.removeTenantId();
        }
    }
}
