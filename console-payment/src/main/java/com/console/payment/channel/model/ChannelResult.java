package com.console.payment.channel.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.console.payment.entity.Channel;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelResult {
    private boolean success;
    /** 渠道业务错误码或本地归一化的远程调用错误码。 */
    private String errorCode;
    /** 是否建议保留原订单状态并在稍后重新主动查询。 */
    private boolean retryable;
    private Integer tenantId;
    private String message;
    private String merchantId;
    private String merchantOrderNo;
    private String orderNo;
    private Integer amount;
    private String currency;
    private Integer status;
    private String payUrl;
    private String payRaw;
    private Channel.AmtUnit amtUnit;//金额单位

    @Getter
    public enum ResultStatus {//订单状态 0、支付成功；1、待支付；2、支付中；3、支付失败；5、已退款
        SUCCESS(0,"成功"),
        PENDING(1, "待处理"),
        PAYING(2, "处理中"),
        FAILED(3,"失败"),
        REFUNDED(4,"退款");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String desc;
        ResultStatus(int value, String desc) {
            this.key = value;
            this.desc = desc;
        }
    }

    /**
     * 组装支付渠道错误信息
     * @param message   错误信息
     */
    public static ChannelResult failed(String message) {
        ChannelResult channelResult = new ChannelResult();
        channelResult.setSuccess(false);
        channelResult.setMessage(message);
        return channelResult;
    }

    /**
     * 从商户订单号获取对应的订单ID
     */
    public Long extractOrderIdByMerchantOrderNo() {
        String[] parts = this.getMerchantOrderNo().split("_");
        return Long.parseLong(parts[3]);
    }
}
