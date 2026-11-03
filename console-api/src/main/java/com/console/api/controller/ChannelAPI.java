package com.console.api.controller;

import com.console.api.service.ChannelAPIService;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelAPI {
    @Resource
    private ChannelAPIService channelAPIService;

    @PostMapping("/paymentChannels")
    @PreAuthorize(ignore = true)
    public Result getPaymentChannels() {
        return Result.success(channelAPIService.getPaymentChannels());
    }

    @PostMapping("/cashOutChannels")
    @PreAuthorize(ignore = true)
    public Result getCashOutChannels() {
        return Result.success(channelAPIService.getCashOutChannels());
    }
}
