package com.console.payment.channel.strategy.fourz.req;

import com.console.framework.utils.TenantUtils;
import com.console.payment.entity.Channel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOrderReq {
    private String  merchantNo;      // 商户ID
    private String  merchantOrderNo; // 商户订单号
    private Integer amount;          // 金额(单位:分)
    private String  code;            // 产品编码
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
        merchantNo = channel.getAppKey();
        merchantOrderNo = serviceCode + "_" + TenantUtils.getTenantId() + "_" + paymentOrder.getMarketSiteId() + "_" + paymentOrder.getId().toString();
        amount = PaymentUtils.toCents(paymentOrder.getOrderAmt());
        code = channel.getPayType();
        currency = channel.getCurType();
        content = "recharge";
        clientIp = paymentOrder.getIp();
        callback = channel.getPayCallBackUrl();
        kycPayerIdNo = paymentOrder.getPayerIdNo();
        kycPayerName = paymentOrder.getPayerName();
        sign = PaymentUtils.sign(this, channel.getApiSecret());
    }
}
