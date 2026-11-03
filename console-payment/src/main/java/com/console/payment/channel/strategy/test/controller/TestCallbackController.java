package com.console.payment.channel.strategy.test.controller;

import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.request.PreAuthorize;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.test.TestStrategy;
import com.console.payment.channel.strategy.u2c.res.CallBackRes;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestCallbackController {
    @Resource
    private TestStrategy testStrategy;

    @PreAuthorize(ignore = true)
    @RequestMapping("/payCallBack/testPay")
    public String payCallBack(CallBackRes callback) {
        ChannelResult channelResult = testStrategy.parsePaymentCallback(callback);
        if (!channelResult.isSuccess()) return "false";
        DynamicTableNameHandler.setTenantId(channelResult.getTenantId());
        if (testStrategy.payCallBackHandle(channelResult)) {
            return "success";
        }
        return "false";
    }
    @PreAuthorize(ignore = true)
    @RequestMapping("/cashOutCallBack/testPay")
    public String cashOutCallBack(CallBackRes callback) {
        ChannelResult channelResult = testStrategy.parseCashOutCallback(callback);
        if (!channelResult.isSuccess()) return "false";
        DynamicTableNameHandler.setTenantId(channelResult.getTenantId());
        if (testStrategy.cashOutCallBackHandle(channelResult)) {
            return "success";
        }
        return "false";
    }
}
