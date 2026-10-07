package com.console.payment.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CashOutOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // 提现订单id
    private Integer orderType; // 订单类型：1、线上出款；2、人工出款
    private Integer walletType; // 出款钱包类型：1、玩家钱包；2、代理钱包
    private String bankCardCode; // 提现卡编码
    private Integer userId; // 提现用户id
    private Integer marketSiteId; // 站点ID
    private Integer marketTeamId; // 团队ID
    private Integer marketerId; // 业务员ID
    private Integer paymentChannelId; // 支付渠道ID
    private String transactionId; // 提现渠道订单id
    private String merchantOrderNo; // 商户订单号
    private Integer status; // 状态，1、订单创建成功等待处理；2、审核通过；3、审核拒绝；4、出款中；5、提现成功；6、提现失败；7、订单取消；8、已退款;9、订单超时
    private BigDecimal orderAmt; // 订单金额
    private BigDecimal fee; // 提现手续费
    private BigDecimal marketerFee; // 渠道服务费
    private BigDecimal surpAmt; // 钱包剩余金额
    private Integer initiatorId; // 人工出款时管理员id
    private LocalDateTime orderTime; // 订单创建时间
    private Integer operatorId; // 审核员id
    private LocalDateTime operatorTime; // 审核时间
    private LocalDateTime cashOutTime; // 渠道商家提现时间
    private Integer cashOutCount; // 提现次数
    private Integer completeState; // 完结状态 1、已完结；0、未完结
    private String remarks; // 备注信息
    private String errorMsg; // 错误信息
    private Integer errorType; // 错误信息类型 1-可展示给手机端的信息 2-不可展示给手机端的信息
}
