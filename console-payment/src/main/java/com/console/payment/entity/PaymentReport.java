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
    private Long       id; // 支付订单汇总表主键id
    private LocalDate  reportDate; // 汇总日期
    private Integer    userId; // 用户id
    private Integer    startRechargeTimes; // 线上发起充值订单数
    private BigDecimal startRechargeAmt; // 线上发起入款总订单金额
    private Integer    startRechargeFailedTimes;//线上发起充值失败订单数
    private Integer    rechargeSuccessTimes; // 线上入款成功单数
    private BigDecimal rechargeSuccessAmt; // 线上入款成功金额
    private Integer    manualRechargeTimes; // 人工入款成功订单数
    private BigDecimal manualRechargeAmt; // 人工入款成功金额
    private BigDecimal channelFee; // 渠道的充值手续费
}
