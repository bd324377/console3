package com.console.payment.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ChannelDTO {
    private Integer id;
    private String icon;            //渠道图标
    private String showName;        //前端展示渠道名称
    private Integer payStatus;      //支付渠道状态：1、开启；0、关闭
    private Integer realNamePayStatus;   //是否实名支付状态：1、实名支付；0、无需实名支付
    private Integer cashOutStatus;  //提现渠道状态：1、开启；0、关闭；
    private BigDecimal minPayAmt;//单笔最小充值金额
    private BigDecimal maxPayAmt;//单笔最大充值金额
    private BigDecimal minCashOutAmt;//单笔最小提现金额
    private BigDecimal maxCashOutAmt;//单笔最大提现金额
    private List<String> payAmtArr;//展示的固定金额数组，多个以分号隔开
    private List<String> cashOutAmtArr;//展示的固定金额数组，多个以分号隔开
}
