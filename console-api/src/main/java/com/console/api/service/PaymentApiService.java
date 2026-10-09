package com.console.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.api.dto.PaymentDTO;
import com.console.payment.entity.PaymentOrder;

public interface PaymentApiService {
    String createPaymentOrder(PaymentDTO.CreatePayOrderReq createPayOrderReq);
    IPage<PaymentOrder> getPaymentOrderRecords(PaymentDTO.SearchPayOrderRes searchPayOrderRes);
}
