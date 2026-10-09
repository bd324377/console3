package com.console.payment.channel.strategy.u2c.req;

import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TenantUtils;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 支付订单DTO(用于创建和查询第三方支付订单)
 */
@Getter
@Setter
public class PayOrderReq {
    private String  merchantId;      // 商户ID
    private String  merchantOrderNo; // 商户订单号
    private Integer amount;          // 金额(单位:分)
    private String  payType;         // 产品编码
    private String  currency;        // 币种
    private String  content;         // 订单内容
    private String  bankCode;        // 银行编码（可选）
    private String  kycPayerIdNo;    // KYC付款人ID（可选）
    private String  kycPayerName;    // KYC付款人名称（可选）
    private String  clientIp;        // 用户IP
    private String  callback;        // 回调地址
    private String  redirect;        // 支付完成跳转地址（可选）
    private String  sign;            // 签名

    public PayOrderReq(PaymentOrder paymentOrder, Channel channel, String serviceCode) {
        this.merchantId      = channel.getAppKey();
        this.merchantOrderNo = serviceCode + "_" + TenantUtils.getTenantId() + "_" + paymentOrder.getMarketSiteId() + "_" + paymentOrder.getId().toString();
        this.amount          = paymentOrder.getOrderAmt().multiply(new BigDecimal("100")).intValue();
        this.payType         = channel.getPayType();
        this.currency        = channel.getCurType();
        this.content         = "recharge";
        this.clientIp        = RequestUtils.getRequestIp();
        this.callback        = RequestUtils.getRequestPrefix() + channel.getPayCallBackUrl();
        this.kycPayerIdNo    = paymentOrder.getPayerIdNo();
        this.kycPayerName    = paymentOrder.getPayerName();
        this.sign = PaymentUtils.sign(this, channel.getApiSecret());
    }
}
