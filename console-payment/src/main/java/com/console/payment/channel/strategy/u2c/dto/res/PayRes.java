package com.console.payment.channel.strategy.u2c.dto.res;

import lombok.Getter;
import lombok.Setter;

/**
 * 支付结果
 */
@Getter
@Setter
public class PayRes {
    private Integer amount;//金额(单位:分)
    private String orderNo;//平台订单号
    private String payType;//代收产品编码
    private String merchantId;//商户 Id
    private String currency;//币种  BRL
    private String payUrl;//支付链接
    private String merchantOrderNo;//商户订单号
    private String payRaw;//支付信息（扫码时为二维码内容，可能为空）
    private String status;//订单状态:WAITING_PAY	待支付;PAYING	支付中;PAID	已支付;PAY_FAILED	支付失败;REFUND	已退款

//    /**
//     * 组装支付结果
//     */
//    public ChannelResult buildPaymentResult() {
//        ChannelResult channelResult = new ChannelResult();
//        channelResult.setSuccess(true);
//        channelResult.setState(Objects.requireNonNull(OrderState.getOrderStateByValue(this.getStatus())).getKey());
//        channelResult.setOrderNo(this.getOrderNo());
//        channelResult.setUrl(this.getPayUrl());
//        channelResult.setMerchantOrderNo(this.getMerchantOrderNo());
//        String[] parts = this.getMerchantOrderNo().split("_");
//        channelResult.setTenantCode(parts[1]);
//        return channelResult;
//    }
}
