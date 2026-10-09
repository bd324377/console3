package com.console.payment.channel.strategy.u2c.controller;

import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.request.PreAuthorize;
import com.console.payment.channel.ChannelContext;
import com.console.payment.channel.ChannelEnum;
import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.u2c.U2CStrategy;
import com.console.payment.channel.strategy.u2c.res.CallBackRes;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class U2CCallbackController {
    @Resource
    private U2CStrategy u2CStrategy;

    @PreAuthorize(ignore = true)
    @RequestMapping("/payCallBack/u2cPay")
    public String payCallBack(CallBackRes callback) {
        ChannelResult channelResult = u2CStrategy.parsePaymentCallback(callback);
        if (!channelResult.isSuccess()) return "false";
        DynamicTableNameHandler.setTenantId(channelResult.getTenantId());
        if (u2CStrategy.payCallBackHandle(channelResult)) {
            return "success";
        }
        return "false";
    }
    @PreAuthorize(ignore = true)
    @RequestMapping("/cashOutCallBack/u2cPay")
    public String cashOutCallBack(CallBackRes callback) {
        ChannelResult channelResult = u2CStrategy.parseCashOutCallback(callback);
        if (!channelResult.isSuccess()) return "false";
        DynamicTableNameHandler.setTenantId(channelResult.getTenantId());
        if (u2CStrategy.cashOutCallBackHandle(channelResult)) {
            return "success";
        }
        return "false";
    }
}
