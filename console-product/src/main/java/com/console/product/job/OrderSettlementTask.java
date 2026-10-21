package com.console.product.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderSettlementTask {//定时做订单结算 1、更新用户下单金额；2、更新订单汇总报告；3、同步到订单表中

}
