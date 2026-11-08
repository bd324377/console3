package com.console.user.model.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class VipBo implements Serializable {//VIP相关信息
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String vipName;//vip等级名称
    private String icon;//图标
    private Integer level;
    private Integer cashOutStatus;//是否能提现：1是0否
    private BigDecimal cashOutRate;//提现手续费率
    private BigDecimal dayCashOutLimit;//日提现限额
    private BigDecimal rechargeRate;//充值手续费
    private String customerCares;//专属客服地址
    private Integer nextVipId;//下一级VIP主键ID
    private BigDecimal upgradeRechargeAmt;//升级所需充值金额
    private BigDecimal upgradeOrderAmt;//升级所需下单金额
}
