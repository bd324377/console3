package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.console.framework.constants.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer orderType;//订单类型 1、线上入款；2、人工入款;
    @NotNull(message = "",groups = ValidationGroup.insert.class)
    private Integer userId;//用户id
    private Integer siteId;//站点ID
    private Integer teamId;       //营销团队ID
    private Integer marketerId;   //营销员ID
    private Long paymentChannelId;//支付渠道ID
    private String transactionId;//充值渠道订单id
    private String merchantOrderNo;//商户订单号
    private Integer status;//订单状态，1为待锁定，2为正在入款，3为入款成功，4为入款失败，5为用户取消；6、审核通过；7、审核失败
    @NotNull(message = "",groups = ValidationGroup.insert.class)
    private BigDecimal orderAmt;//支付订单金额
    private BigDecimal toBeOrderAmt;//待下单金额
    private BigDecimal fee;//充值手续费
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
    @TableField(exist = false)
    private Integer errorType;
}
