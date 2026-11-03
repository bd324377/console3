package com.console.api.dto;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.console.framework.utils.RequestUtils;
import com.console.payment.entity.PaymentOrder;
import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startSearchTime;    //开始查询时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endSearchTime;      //结束查询时间
        private Integer status;//1待处理，2入款中，3成功，4失败，5取消，6审核通过，7审核失败，8退款
    }
}
