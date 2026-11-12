package com.console.api.controller;

import com.console.api.dto.PaymentDTO;
import com.console.api.service.PaymentAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentAPI {
    @Resource
    private PaymentAPIService paymentAPIService;

    /**
     * 线上充值申请接口
     */
    @RequestMapping("/createPaymentOrder")
    public Result createPaymentOrder(@RequestBody PaymentDTO.CreatePayOrderReq createPayOrderReq) {
        return Result.success(paymentAPIService.createPaymentOrder(createPayOrderReq));
    }

    /**
     * 支付订单查询
     */
    @PostMapping("/paymentOrderRecord")
    public Result getPaymentOrderRecord(@RequestBody PaymentDTO.SearchPayOrderReq searchPayOrderReq) {
        return Result.success(paymentAPIService.getPaymentOrderRecords(searchPayOrderReq));
    }
}
