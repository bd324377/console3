package com.console.payment.controller;

import com.console.framework.domain.Result;
import com.console.framework.utils.RequestUtils;
import com.console.payment.model.req.CreateCashOutReq;
import com.console.payment.model.req.CreatePaymentReq;
import com.console.payment.service.PaymentApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentApplicationService paymentApplicationService;

    @PostMapping("/orders")
    public Result createPayment(@Valid @RequestBody CreatePaymentReq request) {
        return Result.success(paymentApplicationService.createPayment(request, RequestUtils.getRequestIp()));
    }

    @GetMapping("/orders/{merchantOrderNo}")
    public Result queryPayment(@PathVariable String merchantOrderNo) {
        return Result.success(paymentApplicationService.queryPayment(merchantOrderNo));
    }

    @PostMapping("/cash-out-orders")
    public Result createCashOut(@Valid @RequestBody CreateCashOutReq request) {
        return Result.success(paymentApplicationService.createCashOut(request));
    }

    @GetMapping("/cash-out-orders/{merchantOrderNo}")
    public Result queryCashOut(@PathVariable String merchantOrderNo) {
        return Result.success(paymentApplicationService.queryCashOut(merchantOrderNo));
    }

    @GetMapping("/channels/{channelId}/balance")
    public Result queryBalance(@PathVariable Long channelId) {
        return Result.success(paymentApplicationService.queryBalance(channelId));
    }
}
