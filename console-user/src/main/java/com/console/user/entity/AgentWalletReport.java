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
@TableName("s_agent_wallet_report")
public class AgentWalletReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 汇总id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 汇总日期
     */
    @TableField("report_date")
    private Integer reportDate;

    /**
     * 代理用户id（邀请人）
     */
    @TableField("inviter_id")
    private Integer inviterId;

    /**
     * 邀新奖励金额
     */
    @TableField("invite_reward_amt")
    private BigDecimal inviteRewardAmt;

    /**
     * 下级返佣金额
     */
    @TableField("rake_back_amt")
    private BigDecimal rakeBackAmt;

    /**
     * 活动奖励金额
     */
    @TableField("activity_reward_amt")
    private BigDecimal activityRewardAmt;

    /**
     * 提现失败回退金额
     */
    @TableField("cash_out_failed_back_amt")
    private BigDecimal cashOutFailedBackAmt;

    /**
     * 在线出款金额
     */
    @TableField("online_cash_out_amt")
    private BigDecimal onlineCashOutAmt;

    /**
     * 人工出款金额
     */
    @TableField("manual_cash_out_amt")
    private BigDecimal manualCashOutAmt;

    /**
     * 转入用户钱包金额
     */
    @TableField("transfer_to_user_wallet_amt")
    private BigDecimal transferToUserWalletAmt;

    /**
     * 提现手续费
     */
    @TableField("cash_out_fee_amt")
    private BigDecimal cashOutFeeAmt;
}
