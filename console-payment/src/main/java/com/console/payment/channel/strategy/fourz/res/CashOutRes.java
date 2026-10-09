package com.console.payment.channel.strategy.fourz.res;

import com.console.payment.channel.model.ChannelResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class CashOutRes {
    private String merchantNo;//商户 Id
    private String merchantOrderNo;//商户订单号
    private String orderNo;//平台订单号
    private String amount;//金额(单位:分)
    private String status;//订单状态:WAITING_PAY	待支付;PAYING	支付中;PAID	已支付;PAY_FAILED	支付失败;REFUND	已退款
    private String currency;//币种  BRL
    private String errorMsg;//错误信息

    public ChannelResult buildCashOutResult() {
        ChannelResult channelResult = new ChannelResult();
        if (StringUtils.hasText(this.getErrorMsg())) {//失败
            channelResult.setSuccess(false);
            channelResult.setMessage(this.getErrorMsg());
        } else {
            channelResult.setSuccess(true);
            channelResult.setStatus(OrderStatus.getOrderStatusByValue(this.getStatus()).getKey());
            channelResult.setOrderNo(this.getOrderNo());
            channelResult.setMerchantOrderNo(this.getMerchantOrderNo());
        }
        return channelResult;
    }
}
