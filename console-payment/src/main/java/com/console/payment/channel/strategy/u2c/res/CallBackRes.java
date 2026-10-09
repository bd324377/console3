package com.console.payment.channel.strategy.u2c.res;

import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.Channel;
import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallBackRes {
    private String merchantId;//商户ID
    private String merchantOrderNo;//商户订单号
    private String orderNo;//平台订单号
    private String amount;//金额(单位:分)
    private String status;//订单状态:WAITING_PAY	待支付;PAYING	支付中;PAID	已支付;PAY_FAILED	支付失败;REFUND	已退款
    private String currency;//币种  BRL
    private String payType;//代收产品编码
    private String ref_cpf;//付款人cpf（非必填）
    private String ref_name;//付款人姓名（非必填）
    private String payRaw;//支付信息（扫码时为二维码内容，可能为空）
    private String errorMsg;//错误信息（失败时返回原因）
    private String sign;    //签名

    public String buildSign(String apiSecret) {
        this.setSign(null);
        return PaymentUtils.sign(this,apiSecret);
    }

    public ChannelResult buildPaymentResult(Channel.AmtUnit amtUnit) {
        ChannelResult channelResult = new ChannelResult();
        channelResult.setSuccess(true);
        channelResult.setMerchantId(merchantId);
        channelResult.setCurrency(currency);
        channelResult.setMessage(errorMsg);
        channelResult.setOrderNo(orderNo);
        channelResult.setMerchantOrderNo(merchantOrderNo);
        channelResult.setAmount(Integer.parseInt(amount));
        channelResult.setStatus(OrderStatus.getOrderStatusByValue(this.getStatus()).getKey());
        String[] parts = this.getMerchantOrderNo().split("_");
        channelResult.setTenantId(Integer.valueOf(parts[1]));
        channelResult.setAmtUnit(amtUnit);
        return channelResult;
    }
}
