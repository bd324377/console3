package com.console.payment.channel.strategy.fourz.res;

import com.console.payment.channel.model.ChannelResult;
import com.console.payment.entity.Channel;
import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallBackRes extends OrderRes {
    private String merchantNo;//商户ID
    private String merchantOrderNo;//商户订单号
    private String orderNo;//平台订单号
    private String amount;//金额(单位:分)
    private String status;//订单状态:WAITING_PAY	待支付;PAYING	支付中;PAID	已支付;PAY_FAILED	支付失败;REFUND	已退款
    private String currency;//币种  BRL
    private String code;//代收产品编码
    private String ref_cpf;//付款人cpf（非必填）
    private String ref_name;//付款人姓名（非必填）
    private String errorMsg;//错误信息（失败时返回原因）
    private String sign;    //签名

    //构建支付结果
    public ChannelResult buildPaymentResult(Channel.AmtUnit amtUnit) {
        ChannelResult channelResult = new ChannelResult();
        channelResult.setSuccess(true);
        channelResult.setOrderNo(orderNo);
        channelResult.setMerchantOrderNo(merchantOrderNo);
        channelResult.setAmount(Integer.parseInt(amount));
        channelResult.setStatus(OrderStatus.getOrderStatusByValue(this.getStatus()).getKey());
        String[] parts = this.getMerchantOrderNo().split("_");
        channelResult.setTenantId(Integer.valueOf(parts[1]));
        channelResult.setAmtUnit(amtUnit);
        return channelResult;
    }

    public String buildSign(String apiSecret) {
        this.setSign(null);
        return PaymentUtils.sign(this,apiSecret);
    }
}
