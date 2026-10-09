package com.console.api.dto;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.console.framework.utils.RequestUtils;
import com.console.payment.entity.PaymentOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {
    @Getter
    @Setter
    public static class CreatePayOrderReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private BigDecimal amount;      //充值金额
        @NotNull
        private Integer    channelId;   //充值通道ID
        private Integer    activityId;  //活动id
        private String     payerIdNo;   //充值人实名ID（）
        private String     payerName;   //充值人姓名
        private Integer    userId;      //用户ID

        public PaymentOrder buildPaymentOrder() {
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setId(IdWorker.getId());
            paymentOrder.setOrderType(1);
            paymentOrder.setChannelId(channelId);
            paymentOrder.setOrderAmt(amount);
            paymentOrder.setActivityId(activityId);
            paymentOrder.setUserId(userId);
            paymentOrder.setOrderTime(LocalDateTime.now());
            paymentOrder.setStatus(PaymentOrder.PaymentStatus.PAYING);
            paymentOrder.setIp(RequestUtils.getRequestIp());                    //设置充值ip
            return paymentOrder;
        }
    }

    /**
     * 支付订单查询参数
     */
    @Getter
    @Setter
    public static class SearchPayOrderRes implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private long    size = 20;          //每页条数
        private long    current = 1;        //当前分页
        private Integer startSearchTime;    //开始查询时间
        private Integer endSearchTime;      //结束查询时间
        private PaymentOrder.PaymentStatus status;
    }
}
