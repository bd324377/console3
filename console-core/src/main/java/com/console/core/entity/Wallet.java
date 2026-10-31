package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_wallet")
public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id; // 钱包id
    private Integer userId;//用户id
    private WalletType walletType;//钱包类型
    private BigDecimal balance; // 钱包余额
    private BigDecimal cashAbleAmt; // 可提现金额
    private BigDecimal toBeOrderAmt; // 待下单金额（完成下单金额指标才能解锁更多体现金额）
    private Integer cashOutTimes; // 钱包提现次数
    private BigDecimal cashOutAmt; // 钱包提现总额
    private BigDecimal manualPayOutAmt; // 人工出款金额
    private BigDecimal dayCashOutAmt; // 钱包日提现总额
    private LocalDateTime lastCashOutTime; // 最新提现时间
    private BigDecimal expendAmt; // 平台内消费金额（如购买套餐卡等）
    private BigDecimal agentTransferAmt; // 代理钱包转账到用户钱包金额
    private Integer agentTransferTimes; // 代理钱包转账到用户钱包次数
    private Integer rechargeTimes; // 充值次数
    private BigDecimal rechargeAmt; // 充值总额
    private BigDecimal manualRechargeAmt; // 人工入款金额（签约费、订单异常补偿等）
    private LocalDateTime lastRechargeTime; // 最新充值时间
    private BigDecimal activityRewardAmt; // 活动赠送金额
    private BigDecimal orderAmt; // 已下单总额
    private BigDecimal orderResultAmt; // 订单结果总额
    private String cashOutPwd; // 提现密码
    private Integer transferStatus; // 与中转平台转账状态 1、正常；2、异常
    private Long transactionId; // 与中转平台转账异常的交易id
    private Integer amtLocation; // 金额所在 0、平台；1、第三方平台；
    private LocalDateTime switchIndexTime; // 切换平台账户类型时间
    private Integer orderMode; // 订单模式
    private Integer orderModeIndex; // 下单模式对应的厂商下标
    private Integer subUserOrderMode; // 下级用户下单模式

    @TableField(exist = false)
    private boolean isSwitchOrderModelIndex;//是否切换下单模式
    @Getter
    public enum WalletType {
        USER_WALLET(0,"用户钱包"),
        TRIAL_WALLET(1,"试玩钱包"),
        AGENT_WALLET(2,"代理钱包"),
        POINTS_WALLET(3,"积分钱包"),
        OTHER_WALLET(4,"其他钱包");

        @JsonValue
        @EnumValue
        private final Integer type;
        private final String value;

        WalletType(int type,String value) {
            this.type = type;
            this.value = value;
        }
    }
}
