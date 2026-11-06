package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.console.core.entity.Wallet;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_cash_out_order")
public class CashOutOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id; // 提现订单id
    private Integer orderType; // 订单类型：1、线上出款；2、人工出款
    private Wallet.WalletType walletType; // 出款钱包类型：1、玩家钱包；2、代理钱包
    private String bankCardCode; // 提现卡编码
    private Integer userId; // 提现用户id
    private Integer marketSiteId; // 站点ID
    private Integer marketTeamId; // 团队ID
    private Integer marketerId; // 业务员ID
    private Integer channelId; // 支付渠道ID
    private String transactionId; // 提现渠道订单id
    private String merchantOrderNo; // 商户订单号
    private CashOutStatus status; // 状态，1、订单创建成功等待处理；2、审核通过；3、审核拒绝；4、出款中；5、提现成功；6、提现失败；7、订单取消；8、已退款;9、订单超时
    private BigDecimal orderAmt; // 订单金额
    private BigDecimal fee; // 用户提现手续费
    private BigDecimal channelFee;//渠道手续费
    private BigDecimal balance; // 钱包剩余金额
    private Integer initiatorId; // 人工出款时管理员id
    private LocalDateTime orderTime; // 订单创建时间
    private Integer operatorId; // 审核员id
    private LocalDateTime operatorTime; // 审核时间
    private LocalDateTime cashOutTime; // 渠道商家提现时间
    private Integer cashOutTimes; // 提现次数
    private Integer completeStatus; // 完结状态 1、已完结；0、未完结
    private Integer riskValue;//风险值
    private String checkReasons;//进入审核状态原因
    private String remarks; // 备注信息
    private String errorMsg; // 错误信息
    private Integer errorType; // 错误信息类型 1-可展示给手机端的信息 2-不可展示给手机端的信息

    @TableField(exist = false)
    private BigDecimal cashOutAmt;
    @TableField(exist = false)
    private BigDecimal pendingReviewAmt;

    @Getter
    public enum CashOutStatus {
        //1待处理，2入款中，3成功，4失败，5取消，6审核通过，7审核失败，8退款
        UNFIND(0,"非正常状态"),
        PENDING(1, "待处理"),
        APPROVED(2,"审核通过"),
        REJECTED(3,"审核失败"),
        CASHOUTING(4, "出款中"),
        SUCCESS(5,"成功"),
        FAILED(6,"提现失败"),
        FALSE_SUCCESS(7,"假出款成功"),//只扣金额不出款
        REFUNDED(8,"已退款"),
        TIMEOUT(9,"订单超时"),
        SUSPEND(10,"卡单审核");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String desc;
        CashOutStatus(int value, String desc) {
            this.key = value;
            this.desc = desc;
        }
    }

    @Getter
    public enum CheckReason {//订单进入审核的原因
        UNCHECK(0,"无需审核"),
        PERSONLIMIT(1,"个人设置限制提现"),
        LEVELLIMIT(2,"等级限制"),
        PLATAMTLIMIT(3,"平台额度限制");
        @JsonValue
        @EnumValue
        private final Integer key;
        private final String desc;
        CheckReason(int value, String desc) {
            this.key = value;
            this.desc = desc;
        }
    }
}
