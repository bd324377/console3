package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@TableName("s_payment_report")
public class PaymentReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer    id; // 支付订单汇总表主键id
    private LocalDate  reportDate; // 汇总日期
    private Integer    userId; // 用户id
    private Integer    startOnlineTimes; // 线上发起充值订单数
    private BigDecimal startOnlineAmt; // 线上发起入款总订单金额
    private Integer    onlineSuccessTimes; // 线上入款成功单数
    private BigDecimal onlineSuccessAmt; // 线上入款成功金额
    private Integer    manualSuccessTimes; // 人工入款成功订单数
    private BigDecimal manualSuccessAmt; // 人工入款成功金额
    private Integer    resupplySuccessTimes; // 补单成功数
    private BigDecimal resupplySuccessAmt; // 补单成功金额
    private BigDecimal fee; // 平台手续费
    private BigDecimal channelFee; // 渠道的充值手续费
    private BigDecimal marketerFee; // 分销渠道的充值手续费
}
