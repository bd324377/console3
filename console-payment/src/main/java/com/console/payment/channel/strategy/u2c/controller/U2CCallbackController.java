package com.console.payment.channel.strategy.u2c.controller;

import com.console.framework.request.PreAuthorize;
import com.console.framework.utils.RequestUtils;
import com.console.payment.service.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * U2C 渠道异步通知入口。
 *
 * <p>回调无需登录，但业务层仍会验证来源 IP、签名、商户号、订单号、币种和金额。
 * 只有本地事务处理成功后才返回 U2C 要求的纯文本 {@code success}。</p>
 */
@RestController
@RequestMapping("/payment/callback/u2c")
@RequiredArgsConstructor
public class U2CCallbackController {
    private final PaymentApplicationService paymentApplicationService;

    /** 处理充值结果通知。 */
    @PreAuthorize(ignore = true)
    @PostMapping(value = "/pay", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String payment(@RequestBody Map<String, Object> payload) {
        paymentApplicationService.handlePaymentCallback(payload, RequestUtils.getRequestIp());
        return "success";
    }

    /** 处理提现结果通知。 */
    @PreAuthorize(ignore = true)
    @PostMapping(value = "/cash-out", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String cashOut(@RequestBody Map<String, Object> payload) {
        paymentApplicationService.handleCashOutCallback(payload, RequestUtils.getRequestIp());
        return "success";
    }
}
