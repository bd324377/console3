package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@TableName("s_cash_out_report")
public class CashOutReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @com.baomidou.mybatisplus.annotation.TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Integer id; // 日汇总订单id
    private LocalDate reportDate; // 汇总日期
    private Integer userId; // 用户id
    private Integer walletType; // 出款钱包类型：1、玩家钱包；2、代理钱包
    private Integer startOnlineTimes; // 线上发起提现订单数
    private BigDecimal startOnlineAmt; // 线上发起出款总订单金额
    private Integer onlineSuccessTimes; // 线上成功提现单数
    private BigDecimal onlineSuccessAmt; // 线上成功提现金额
    private Integer falseSuccessTimes; // 假出款提现单数
    private BigDecimal falseSuccessAmt; // 假出款提现金额
    private Integer manualSuccessTimes; // 人工提现陈工单数
    private BigDecimal manualSuccessAmt; // 人工提现陈工金额
    private BigDecimal fee; // 提现手续费
    private BigDecimal channelFee; // 渠道手续费
    private BigDecimal marketerFee; // 分销渠道手续费
}
