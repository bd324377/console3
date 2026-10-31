package com.console.product.task;

import com.console.framework.constants.CacheConstants;
import com.console.framework.task.DistributedThrottledTaskExecutor;
import com.console.product.service.TransferService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferSyncTask {//定时同步中转平台的信息
    @Value("${scheduled.sync.product.enabled}")
    private boolean enabledSyncProduct;
    @Value("${scheduled.sync.productOrder.enabled}")
    private boolean enabledSyncProductOrder;

    @Resource
    private TransferService transferService;
    @Resource
    private DistributedThrottledTaskExecutor executor;

    /**
     * 产品同步
     */
    @Scheduled(cron = "${scheduled.sync.product.cron}")
    public void executeProductSync() {
        if (enabledSyncProduct) {
            executor.runQuietly("同步中转平台产品列表",
                    CacheConstants.ACQUIRE_LOCK_SYNC_PRODUCT,
                    30_000L,
                    ignored -> transferService.fetchProductData(),
                    null);
        }
    }


    /**
     * 产品订单同步
     */
    @Scheduled(cron = "${scheduled.sync.productOrder.cron}")
    public void executeProductProductOrderSync() {
        if (enabledSyncProductOrder) {
            executor.runQuietly("同步中转平台订单列表",
                    CacheConstants.ACQUIRE_LOCK_SYNC_PRODUCT_ORDER,
                    10_000L,
                    ignored -> transferService.fetchProductOrderData(),
                    null);
        }
    }
}
