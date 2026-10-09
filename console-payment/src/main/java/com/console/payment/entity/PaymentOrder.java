package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.console.framework.constants.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_payment_order")
public class PaymentOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer orderType;//订单类型 1、线上入款；2、人工入款;
    @NotNull(message = "",groups = ValidationGroup.insert.class)
    private Integer userId;//用户id
    private Integer marketSiteId;//站点ID
    private Integer marketTeamId;       //营销团队ID
    private Integer marketerId;   //营销员ID
    private Integer channelId;//支付渠道ID
    private Integer activityId;  //参与活动id
    private String transactionId;//充值渠道订单id
    private String merchantOrderNo;//商户订单号
    private PaymentStatus status;//1待处理，2入款中，3成功，4失败，5取消，6审核通过，7审核失败，8退款
    @NotNull(message = "",groups = ValidationGroup.insert.class)
    private BigDecimal orderAmt;//支付订单金额
    private BigDecimal toBeOrderAmt;//待下单金额
    private BigDecimal fee;//充值手续费
    private BigDecimal channelFee;//渠道手续费
    private BigDecimal marketerFee; // 业务服务费
    private LocalDateTime orderTime;//订单生成时间
    private String payUrl;//拼装好的产商支付url，前端根据这个url转成支付二维码
    private LocalDateTime payTime;//订单支付时间
    private String ip;//充值ip
    private String advertId;//广告ID
    private Integer initiatorId;//人工入款发起人ID（后台人员）
    private Integer operatorId;//操作人ID（审核/拒审等后台操作人员ID）
    private LocalDateTime operatorTime;//操作时间
    private Integer rechargeTimes;//充值次数
    private String errorMsg;//错误信息
    private Integer completeState;//完结状态 1、已完结；0、未完结;
    private Integer errorType;
    private String remarks;
    private Boolean resupply;

    @TableField(exist = false)
    private String payerIdNo;
    @TableField(exist = false)
    private String payerName;

    @Getter
    public enum PaymentStatus {
        //1待处理，2入款中，3成功，4失败，5取消，6审核通过，7审核失败，8退款
        UNFIND(0,"非正常状态"),
        PENDING(1, "待处理"),
        PAYING(2, "入款中"),
        SUCCESS(3,"成功"),
        FAILED(4,"失败"),
        CANCEL(5,"取消"),
        APPROVED(6,"审核通过"),
        REJECTED(7,"审核失败"),
        REFUNDED(8,"退款");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String desc;
        PaymentStatus(int value, String desc) {
            this.key = value;
            this.desc = desc;
        }
    }
}
