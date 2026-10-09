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
@TableName("p_cash_out_proc")
public class CashOutProc implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id; // 管理员(操作人id)id
    private Long orderId; // 订单id
    private Integer userId; // 用户id
    private Integer channelId; // 渠道id
    private Integer walletType; // 钱包类型
    private BigDecimal orderAmt; // 订单金额
    private BigDecimal fee; // 提现手续费
    private BigDecimal marketerFee; // 渠道服务费
    private Integer procType; // 操作类型
    private LocalDate procDate; // 过程发生日期
    private LocalDateTime procTime; // 过程发生时间
    private Integer operatorId; // 管理员(操作人)id

    @TableField(exist = false)
    private Integer tenantId;
    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;

    @Getter
    public enum ProcType {
        // 操作类型：1、线上发起出款；2、后台人工发起出款；3.线上出款成功；4、线上出款失败"；5、线上出款成功但只减账户金额不出款；6、人工出款成功；
        // 7、人工出款失败；8、人工出款成功但只减账户金额不出款；9、线上发起出款失败；10、后台人工发起出款失败；11、用户取消
        UNDEFINED(0, "未定义操作状态"),
        START_ONLINE_CASH_OUT(1, "线上发起出款"),
        START_MANUAL_CASH_OUT(2, "后台人工发起出款"),
        ONLINE_CASH_OUT_SUCCESS(3, "线上出款成功"),
        ONLINE_CASH_OUT_FAILED(4, "线上出款失败"),
        ONLINE_FALSE_CASH_OUT_SUCCESS(5,"线上假出款成功"),//用于刷单用户--线上出款成功但只减账户金额不出款
        MANUAL_CASH_OUT_SUCCESS(6, "人工出款成功"),
        MANUAL_CASH_OUT_FAILED(7, "人工出款失败"),
        START_ONLINE_CASH_OUT_FAILED(8, "线上发起出款失败"),
        START_MANUAL_CASH_OUT_FAILED(9, "后台人工发起出款失败"),
        USER_CANCEL(10, "用户取消"),
        APPROVED(11,"审核通过"),
        REVIEW_FAILED(12,"审核不通过(金额退回)"),
        CANCEL_REVIEW(13,"取消审核"),
        ORDER_SUSPENDED(14,"订单挂起");

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
