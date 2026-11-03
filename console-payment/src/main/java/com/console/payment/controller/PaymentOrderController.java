package com.console.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.framework.domain.Result;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.model.PaymentOrderReq;
import com.console.payment.service.PaymentOrderService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paymentOrder")
public class PaymentOrderController {
    @Resource
    private PaymentOrderService paymentOrderService;

    /** 人工入款 **/
    @PostMapping
    public Result createPaymentOrder(@RequestBody @Validated PaymentOrderReq.CreatePaymentOrderReq createPaymentOrderReq) {
        return paymentOrderService.createPaymentOrder(createPaymentOrderReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getPaymentOrderPage(Page<PaymentOrder> page, PaymentOrderReq.SearchPaymentOrderReq searchPaymentOrderReq) {
        return Result.success(paymentOrderService.getPaymentOrderPage(page,searchPaymentOrderReq));
    }
}
