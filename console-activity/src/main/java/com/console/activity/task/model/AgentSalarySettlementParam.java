package com.console.activity.task.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgentSalarySettlementParam {
    private Integer subUserCount;       //下级用户人数
    private BigDecimal subRechargeAmt;  //下级充值金额
}
