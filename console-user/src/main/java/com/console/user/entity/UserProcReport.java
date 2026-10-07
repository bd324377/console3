package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author aha
 * @since 2024-09-24
 */
@Getter
@Setter
@TableName("s_user_proc_report")
public class UserProcReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 汇总主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 汇总日期
     */
    @TableField("report_date")
    private Integer reportDate;

    /**
     * 登录次数（大于0即为登录状态）
     */
    @TableField("login_times")
    private Integer loginTimes;

    /**
     * 注册状态:1当天注册的，0已注册
     */
    @TableField("register_state")
    private Integer registerState;

    /**
     * 充值次数
     */
    @TableField("recharge_times")
    private Integer rechargeTimes;

    /**
     * 充值金额
     */
    @TableField("recharge_amt")
    private BigDecimal rechargeAmt = new BigDecimal("0");

    /**
     * 激活状态（达到人头奖人数）1是；0否
     */
    @TableField("activate_state")
    private Integer activateState;

    /**
     * 首充状态:1是，0：否
     */
    @TableField("first_recharge_state")
    private Integer firstRechargeState;

    /**
     * 首充金额
     */
    @TableField("first_recharge_amt")
    private BigDecimal firstRechargeAmt;

    /**
     * 用户钱包提现金额
     */
    @TableField("user_cash_out_amt")
    private BigDecimal userCashOutAmt;

    /**
     * 用户起那波提现次数（大于0则为提现状态）
     */
    @TableField("user_cash_out_times")
    private Integer userCashOutTimes;

    /**
     * 代理钱包提现金额
     */
    @TableField("agent_cash_out_amt")
    private BigDecimal agentCashOutAmt;

    /**
     * 代理钱包提现次数（大于0则为提现状态）
     */
    @TableField("agent_cash_out_times")
    private Integer agentCashOutTimes;

    /**
     * 累计实际下单金额
     */
    @TableField("order_amt")
    private BigDecimal orderAmt = new BigDecimal(0);

    /**
     * 累计平台规则下单金额
     */
    @TableField("order_ans_amt")
    private BigDecimal orderAnsAmt;

    /**
     * 累计订单结果金额
     */
    @TableField("order_result_amt")
    private BigDecimal orderResultAmt;

    /**
     * 累计订单平台支出金额
     */
    @TableField("order_payout_amt")
    private BigDecimal orderPayoutAmt;
}
