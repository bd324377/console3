package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SubPerformanceReport {
    private Integer userId;         //用户ID
    private Integer subUserCount;   //下级用户数
    private BigDecimal orderAmt;    //下单金额
    private BigDecimal orderResultAmt;//订单盈亏
    private BigDecimal rechargeAmt;//充值金额
}
