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
@TableName("s_wallet_report")
public class WalletReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 汇总id */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /** 汇总日期 */
    private LocalDate reportDate;
    /** 用户id */
    private Integer userId;
    /** 钱包类型 */
    private Integer walletType;
    /** 线上充值金额 */
    private BigDecimal rechargeAmt;
    /** 人工入款金额 */
    private BigDecimal manualRechargeAmt;
    /** 活动奖励金额 */
    private BigDecimal activityRewardAmt;
    /** 来自代理钱包转账金额 */
    private BigDecimal agentWalletTransferAmt;
    /** 在线提款金额 */
    private BigDecimal cashOutAmt;
    /** 人工出款金额 */
    private BigDecimal manualCashOutAmt;
    /** 提现返回金额 */
    private BigDecimal cashOutFailedBackAmt;
    /** 转入第三方金额 */
    private BigDecimal transferToThirdParty;
    /** 第三方转出金额 */
    private BigDecimal transferFromThirdParty;
    /** 用户在平台消费金额 */
    private BigDecimal userExpendAmt;
}
