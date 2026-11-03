package com.console.payment.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentOrderReq {
    @Getter
    @Setter
    public static class CreatePaymentOrderReq {
        @NotNull
        private Integer    userId;//用户id
        @NotNull
        private BigDecimal orderAmt;//支付订单金额
        @NotNull
        private BigDecimal toBeOrderAmt;//待下单金额
    }

    @Getter
    @Setter
    public static class SearchPaymentOrderReq {
        private LocalDateTime startSearchTime;
        private LocalDateTime endSearchTime;
        private Integer userId;
        private Integer orderType;     //订单类型
        private Integer channelId;     //支付渠道ID
        private Integer orderStatus;   //订单状态
        private String  transactionId;  //充值渠道订单流水
        private String  merchantOrderNo;//商户订单号
        private Integer marketSiteId;  //站点ID
        private Integer marketTeamId;  //营销团队ID
        private Integer marketerId;    //营销员ID
    }
}
