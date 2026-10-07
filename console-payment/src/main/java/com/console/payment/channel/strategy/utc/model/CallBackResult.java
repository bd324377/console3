package com.console.payment.channel.strategy.utc.model;

import com.alibaba.fastjson.JSON;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.TreeMap;

@Getter
@Setter
public class CallBackResult {
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
        TreeMap<String, Object> paramsMap = PaymentUtils.convert(this, true, false);
        return PaymentUtils.serialMd5Sign(paramsMap,apiSecret);
    }

    public static void main(String[] args) {
        //支付回调样例
        CallBackResult payCallBackResult = new CallBackResult();
        payCallBackResult.setMerchantId("101021");
        payCallBackResult.setMerchantOrderNo("6_31_1975997789670735874");
        payCallBackResult.setOrderNo("F2509288ob7u4a");
        payCallBackResult.setAmount("1000");
        payCallBackResult.setStatus("PAID");
        payCallBackResult.setCurrency("BRL");
        payCallBackResult.setPayType("PIX_QRCODE");
        String sign = payCallBackResult.buildSign("a1KwASByqc31Q0bbSUQs3qAXnesCndcc");
        System.out.println("支付回调签名结果:"+sign);
        //提现回调样例
        CallBackResult cashOutCallBackResult = new CallBackResult();
        cashOutCallBackResult.setMerchantId("101021");
        cashOutCallBackResult.setMerchantOrderNo("6_39_1988623837811322882");
        cashOutCallBackResult.setOrderNo("F251112dtc5hih");
        cashOutCallBackResult.setAmount("1000");
        cashOutCallBackResult.setStatus("PAY_FAILED");
        sign = cashOutCallBackResult.buildSign("a1KwASByqc31Q0bbSUQs3qAXnesCndcc");
        System.out.println("提现回调签名结果:"+sign);
        cashOutCallBackResult.setSign("0e96aaf94a35ef56b560275a1913d255");
        cashOutCallBackResult.setErrorMsg("Erro de processamento, transação não executada. Reenvie a transação");
        cashOutCallBackResult.setCurrency("currency");
        ChannelResult channelResult = cashOutCallBackResult.buildPaymentResult();
        System.out.println("提现结果：" + JSON.toJSONString(channelResult));
    }

    //构建支付结果
    public ChannelResult buildPaymentResult() {
        ChannelResult channelResult = new ChannelResult();
        channelResult.setSuccess(true);
        channelResult.setOrderNo(orderNo);
        channelResult.setMerchantOrderNo(merchantOrderNo);
        channelResult.setAmount(Integer.parseInt(amount));
        channelResult.setState(OrderState.getOrderStateByValue(this.getStatus()).getKey());
        String[] parts = this.getMerchantOrderNo().split("_");
        channelResult.setTenantCode(parts[1]);
        return channelResult;
    }
}
