package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("p_payment_proc")
public class PaymentProc implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id; // 过程id
    private Long orderId; // 订单id
    private Integer userId; // 用户id
    private Integer channelId; // 渠道id
    private BigDecimal orderAmt; // 订单金额
    private BigDecimal channelFee; // 渠道服务费
    private ProcType procType; // 操作类型：1、线上发起入款；2、后台人工发起入款；3.线上入款成功；4、人工补单；5、后台入款成功；6、发起入款失败；7、后台定义入款失败；8、用户取消；
    private LocalDate procDate; // 过程发生日期
    private LocalDateTime procTime; // 过程发生时间
    private Integer operatorId; // 管理员id
    private String remark;

    @TableField(exist = false)
    private Integer tenantId;
    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;

    public PaymentProc() {}

    public PaymentProc(PaymentOrder paymentOrder,ProcType procType) {
        this.orderId = paymentOrder.getId();
        this.userId = paymentOrder.getUserId();
        this.channelId = paymentOrder.getChannelId();
        this.orderAmt = paymentOrder.getOrderAmt();
        this.channelFee = paymentOrder.getChannelFee();
        this.procType = procType;
        this.procDate = LocalDate.now();
        this.procTime = LocalDateTime.now();
    }
    @Getter
    public enum ProcType {
        UNDEFINED(0, "未定义操作状态"),
        START_ONLINE_PAYMENT(1, "线上发起入款"),
        START_MANUAL_PAYMENT(2, "后台发起人工入款"),
        START_ONLINE_PAYMENT_FAILED(3, "线上发起入款失败"),
        START_MANUAL_PAYMENT_FAILED(4, "后台发起人工入款失败"),
        ONLINE_PAYMENT_SUCCESS(5, "线上入款成功"),
        RESUPPLY_PAYMENT_SUCCESS(6, "人工补单成功"),
        MANUAL_PAYMENT_SUCCESS(7, "人工入款成功"),
        ONLINE_PAYMENT_FAILED(8, "线上入款失败"),
        MANUAL_PAYMENT_FAILED(9, "人工入款失败"),
        USER_CANCEL(10,"用户取消"),
        APPROVED(11,"审核通过"),
        REVIEW_FAILED(12,"审核不通过"),
        CANCEL_REVIEW(13,"取消审核");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        ProcType(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static ProcType getProcTypeByKey(int key) {
            for (ProcType type : values()) {
                if (type.getKey() == key) {
                    return type;
                }
            }
            return null;
        }
    }
}
