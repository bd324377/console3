package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SelfInfoRes {
    private String  account;                //账号
    private Integer userId;                 //用户ID
    private String  avatar;                 //头像

    private String  vipName;                //VIP名称
    private String  vipIcon;                //VIP图标
    private BigDecimal orderAmt;            //当前下单金额
    private BigDecimal upgradeOrderAmt;     //VIP升级要求下单金额
    private BigDecimal rechargeAmt;         //当前充值金额
    private BigDecimal upgradeRechargeAmt;  //VIP升级要求充值金额

    private String phone;
    private Integer phoneStatus;

    private boolean hasCashOutPwd;          //是否设置过提现密码
    private Integer pushSubscriptionStatus;//推送订阅状态
}
