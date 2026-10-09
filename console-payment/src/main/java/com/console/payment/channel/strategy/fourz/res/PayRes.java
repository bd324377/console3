package com.console.payment.channel.strategy.fourz.res;

import com.console.payment.channel.model.ChannelResult;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class PayRes {
    private String merchantNo;//商户 Id
    private String merchantOrderNo;//商户订单号
    private String orderNo;//平台订单号
    private String payInfo;//支付链接
    private String raw;//支付信息（扫码时为二维码内容，可能为空）
    private String amount;//金额(单位:分)
    private String status;//订单状态:WAITING_PAY	待支付;PAYING	支付中;PAID	已支付;PAY_FAILED	支付失败;REFUND	已退款
    private String currency;//币种  BRL
    private String code;//代收产品编码

    /**
     * 组装支付结果
     */
    public ChannelResult buildPaymentResult() {
        ChannelResult channelResult = new ChannelResult();
        channelResult.setSuccess(true);
        channelResult.setStatus(Objects.requireNonNull(OrderStatus.getOrderStatusByValue(this.getStatus())).getKey());
        channelResult.setOrderNo(this.getOrderNo());
        channelResult.setPayUrl(this.getPayInfo());
        channelResult.setMerchantOrderNo(this.getMerchantOrderNo());
        String[] parts = this.getMerchantOrderNo().split("_");
        channelResult.setTenantId(Integer.valueOf(parts[1]));
        return channelResult;
    }
}
