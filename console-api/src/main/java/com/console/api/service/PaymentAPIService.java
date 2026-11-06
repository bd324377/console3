package com.console.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.api.dto.PaymentDTO;
import com.console.api.dto.res.PaymentOrderPageRes;
import com.console.payment.entity.PaymentOrder;

public interface PaymentAPIService {
    String createPaymentOrder(PaymentDTO.CreatePayOrderReq createPayOrderReq);
    PaymentOrderPageRes getPaymentOrderRecords(PaymentDTO.SearchPayOrderRes searchPayOrderRes);
}
