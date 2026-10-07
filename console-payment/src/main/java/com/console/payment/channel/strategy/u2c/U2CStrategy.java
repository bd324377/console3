package com.console.payment.channel.strategy.u2c;

import com.console.framework.utils.TenantUtils;
import com.console.payment.channel.PaymentStrategy;
import com.console.payment.entity.PaymentChannel;
import com.console.payment.entity.PaymentOrder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class U2CStrategy extends PaymentStrategy {

    public Map<String,Object> buildRequestParam(PaymentOrder paymentOrder, PaymentChannel channel, String serviceCode) {
        TreeMap<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put("merchantId",channel.getAppKey());
        paramsMap.put("merchantOrderNo",serviceCode + "_" + TenantUtils.getTenantId() + "_" + paymentOrder.getId().toString());
        paramsMap.put("amount",paymentOrder.getOrderAmt().multiply(new BigDecimal("100")).intValue());
        paramsMap.put("payType",channel.getPayType());
        paramsMap.put("currency",channel.getCurType());
        paramsMap.put("content","content");
//        paramsMap.put("clientIp", RequestUtils.getCurrentIp());
//        paramsMap.put("callback",RequestUtils.getRequestPrefix() + channel.getPayBackUrl());
//        paramsMap.put("kycPayerIdNo",paymentOrder.getPayerIdNo());
//        paramsMap.put("kycPayerName",paymentOrder.getPayerName());
//        this.sign = PaymentUtils.serialMd5Sign(paramsMap,channel.getApiSecret());
//        paramsMap.put("sign",this.sign);
        return paramsMap;
    }
}
