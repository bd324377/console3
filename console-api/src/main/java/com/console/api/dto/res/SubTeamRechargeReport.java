package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SubTeamRechargeReport {
    private BigDecimal rechargeAmt;//总充值
    private BigDecimal oneLayerRechargeAmt;//直属下级总充值
    private BigDecimal otherLayerRechargeAmt;//其他层级总充值
    private Integer firstRechargeUserCount;//首充人数
    private Integer oneLayerFirstRechargeUserCount;//直属下级首充人数
    private Integer otherLayerFirstRechargeUserCount;//其他层级首充人数
}
