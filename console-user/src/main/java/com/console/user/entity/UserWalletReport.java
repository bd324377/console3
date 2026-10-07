package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@TableName("s_user_wallet_report")
public class UserWalletReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer reportDate;//汇总日期
    private Long userId;//用户id
    private BigDecimal onlineRechargeAmt;//线上充值金额0
    private BigDecimal resupplyRechargeAmt;//补单成功金额1
    private BigDecimal manualRechargeAmt;//人工入款金额2
    private BigDecimal activityRewardAmt;//活动奖励金额6
    private BigDecimal cashBackAmt;//返现金额7
    private BigDecimal cashOutFailedBackAmt;//提现返回金额3

//    /**
//     * 渠道手续费
//     */
//    @TableField("channel_fee")
//    private BigDecimal channelFee;

    private BigDecimal onlineCashOutAmt;//在线提款金额10
    private BigDecimal manualCashOutAmt;//人工出款金额11
    private BigDecimal cashOutFee;//提现手续费12
    private BigDecimal transferFromAgentWalletAmt;//来自代理钱包转账金额9
    private BigDecimal upgradeCashRewardAmt;//vip等级升级现金奖励金额15

    /**
     * 用户在平台消费金额16
     */
    @TableField("user_expend_amt")
    private BigDecimal userExpendAmt;

    /**
     * 公积金金额
     */
    @TableField("provident_fund_amt")
    private BigDecimal providentFundAmt;

    /**
     * 公积金提取金额
     */
    @TableField("transfer_from_provident_fund_amt")
    private BigDecimal transferFromProvidentFundAmt;

    /**
     * 日返现金额
     */
    @TableField("day_cash_back_amt")
    private BigDecimal dayCashBackAmt;

    /**
     * 周返现金额
     */
    @TableField("week_cash_back_amt")
    private BigDecimal weekCashBackAmt;
}
