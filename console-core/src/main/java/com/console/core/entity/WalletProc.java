package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.console.framework.utils.TenantUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 与资金更新同事务保存，业务键保证同一订单动作只记账一次。 */
@Getter
@Setter
@TableName("p_wallet_proc")
public class WalletProc implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id; // 用户钱包过程id
    private Long relatedId; // 过程产生的关联ID（充值提现则关联订单ID，中转进出则关联转账id）
    private Integer userId; // 钱包id
    private Wallet.WalletType walletType;//钱包类型
    private Integer type; // 变动类型：1、增加；2、减少
    private ProcType procType; // 过程类型
    private BigDecimal beforeAmt; // 变动前钱包金额
    private BigDecimal afterAmt; // 变动后钱包金额
    private BigDecimal amt; // 钱包过程本金金额
    private BigDecimal toBeOrderAmt; // 下单要求金额（本金待下单金额）
    private LocalDate procDate; // 过程发生日期
    private LocalDateTime procTime; // 过程发生时间
    private String description; // 变动原因说明
    private String remark; // 备注
    @TableField(exist = false)
    private Integer procCount;
    @TableField(exist = false)
    private Integer tenantId;
    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;

    public WalletProc() {}

    public WalletProc(Long relatedId,Integer userId,Wallet.WalletType walletType,Integer type,
                      ProcType procType,BigDecimal afterAmt,BigDecimal amt,BigDecimal toBeOrderAmt) {
        this.relatedId = relatedId;
        this.userId = userId;
        this.walletType = walletType;
        this.type = type;
        this.procType = procType;
        this.afterAmt = afterAmt;
        this.amt = amt;
        this.toBeOrderAmt = toBeOrderAmt;
        if (type == 1) {  //新增
            this.beforeAmt = afterAmt.subtract(amt);
        } else { //减少
            this.beforeAmt = afterAmt.add(amt);
        }
        this.procDate = LocalDate.now();
        this.procTime = LocalDateTime.now();
        this.tenantId = TenantUtils.getTenantId();

    }
    @Getter
    public enum ProcType {
        ONLINE_RECHARGE(0, "线上充值","walletProc.description.onlineRecharge"),
        MANUAL_RECHARGE(1, "人工入款","walletProc.description.manualRecharge"),
        ACTIVITY_REWARD(2, "活动奖励","walletProc.description.activityReward"),
        TRANSFER_TO_USER_WALLET(3, "代理钱包转入用户钱包","walletProc.description.transferFromAgentWallet"),
        ONLINE_CASH_OUT(4, "线上提款","walletProc.description.onlineCashOut"),
        MANUAL_PAY_OUT(5, "人工下分","walletProc.description.manualCashOut"),
        CASH_OUT_FAILED_REFUND(6, "提现失败退回","walletProc.description.cashOutFailedBack"),
        TRANSFER_TO_THIRD_PARTY(7, "转入第三方","walletProc.description.toTransfer"),
        TRANSFER_FROM_THIRD_PARTY(8, "第三方转出","walletProc.description.fromTransfer"),
        USER_EXPEND_AMT(9,"用户平台内消费金额","");

        @EnumValue
        @JsonValue
        private final Integer key;
        private final String value;
        private final String langKey;

        ProcType(int key, String value, String langKey) {
            this.key   = key;
            this.value = value;
            this.langKey = langKey;
        }
    }
}
