package com.console.payment.channel.strategy.fourz.controller;

import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.request.PreAuthorize;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.fourz.FourZStrategy;
import com.console.payment.channel.strategy.fourz.res.CallBackRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FourZCallbackController {
    private final FourZStrategy fourZStrategy;

    @PreAuthorize(ignore = true)
    @PostMapping(value = "/payCallBack/4zPay", produces = MediaType.TEXT_PLAIN_VALUE)
    public String payCallback(@RequestBody(required = false) CallBackRes callback) {
        ChannelResult channelResult = fourZStrategy.parsePaymentCallback(callback);
        if (!channelResult.isSuccess()) return "false";
        DynamicTableNameHandler.setTenantId(channelResult.getTenantId());
        if (fourZStrategy.payCallBackHandle(channelResult)) {
            return "success";
        }
        return "false";
    }

    @PreAuthorize(ignore = true)
    @PostMapping(value = "/cashOutCallBack/4zPay", produces = MediaType.TEXT_PLAIN_VALUE)
    public String cashOutCallback(@RequestBody(required = false) CallBackRes callback) {
        ChannelResult channelResult = fourZStrategy.parseCashOutCallback(callback);
        if (!channelResult.isSuccess()) return "false";
        DynamicTableNameHandler.setTenantId(channelResult.getTenantId());
        if (fourZStrategy.cashOutCallBackHandle(channelResult)) {
            return "success";
        }
        return "false";
    }
}
