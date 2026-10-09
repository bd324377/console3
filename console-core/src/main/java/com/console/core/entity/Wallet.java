package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_user")
public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id")
    private Integer id; // 会员钱包id（同userID）
    private BigDecimal userAmt; // 钱包金额
    private BigDecimal cashAbleAmt; // 可提现金额
    private BigDecimal toBeOrderAmt; // 待下单金额（完成下单金额指标才能解锁更多体现金额）
    private BigDecimal agentAmt; // 代理钱包金额
    private BigDecimal points; // 用户积分
    private BigDecimal consumedPoints; // 已消费积分
    private Integer userCashOutTimes; // 用户钱包提现次数
    private BigDecimal userCashOutAmt; // 用户钱包提现总额
    private LocalDateTime lastUserCashOutTime; // 最新用户提现时间
    private BigDecimal userExpendAmt; // 用户钱包平台内消费金额（如购买套餐卡等）
    private BigDecimal userManualPayOutAmt; // 用户钱包人工出款金额
    private Integer agentCashOutTimes; // 代理钱包提现次数
    private BigDecimal agentCashOutAmt; // 代理钱包提现总额
    private LocalDateTime lastAgentCashOutTime; // 最新代理提现时间
    private BigDecimal agentManualPayOutAmt; // 代理钱包人工出款金额
    private BigDecimal agentTransferAmt; // 代理钱包转账到用户钱包金额
    private Integer agentTransferTimes; // 代理钱包转账到用户钱包次数
    private Integer rechargeTimes; // 充值次数
    private BigDecimal rechargeAmt; // 充值总额
    private LocalDateTime lastRechargeTime; // 最新充值时间
    private BigDecimal userRewardAmt; // 用户钱包赠送金额（活动，升级，返现等赠送金额）
    private BigDecimal agentRewardAmt; // 代理钱包赠送金额
    private BigDecimal manualAmt; // 人工入款金额（签约费、订单异常补偿等）
    private BigDecimal resupplyAmt; // 补单成功金额
    private BigDecimal orderedAmt; // 已下单总额
    private BigDecimal orderResultAmt; // 订单结果总额
    private String cashOutPwd; // 提现密码
    private BigDecimal afterUpRechargeAmt; // 升级后已充值金额
    private BigDecimal afterUpOrderedAmt; // 升级后已下单金额
    private Integer transferState; // 与中转平台转账状态 1、正常；2、异常
    private Long transactionId; // 与中转平台转账异常的交易id
    private Integer amtLocation; // 金额所在 0、平台；1、真实金额在第三方平台；2、测试金额在第三方平台
    private LocalDateTime switchIndexTime; // 切换平台账户类型时间
    private Integer orderMode; // 订单模式
    private Integer orderModeIndex; // 下单模式对应的厂商下标
    private Integer subUserOrderMode; // 下级用户下单模式
}
