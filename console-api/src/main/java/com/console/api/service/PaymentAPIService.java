package com.console.api.service;

import com.console.api.dto.PaymentDTO;
import com.console.api.dto.res.PaymentOrderPageRes;

public interface PaymentAPIService {
    String createPaymentOrder(PaymentDTO.CreatePayOrderReq createPayOrderReq);
    PaymentOrderPageRes getPaymentOrderRecords(PaymentDTO.SearchPayOrderReq searchPayOrderReq);
}
