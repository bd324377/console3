package com.console.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.framework.constants.ValidationGroup;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import com.console.payment.entity.Channel;
import com.console.payment.model.ChannelReq;
import com.console.payment.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/channel")
public class ChannelController {
    @Resource
    private ChannelService channelService;

    @PostMapping
//    @PreAuthorize(value = "channel:add",accountType = 1)
    public Result addChannel(@RequestBody @Validated(ValidationGroup.insert.class) Channel channel) {
        return channelService.addChannel(channel) ? Result.success() : Result.failed();
    }

    @DeleteMapping
    public Result delChannel(@RequestBody @Validated ChannelReq.DelChannelReq delChannelReq) {
        return channelService.delChannel(delChannelReq) ? Result.success() : Result.failed();
    }

    @PutMapping("/{field}")
    public Result updateChannel(@PathVariable String field, @RequestBody @Validated ChannelReq.UpdateChannelReq updateChannelReq) {
        return channelService.updateChannel(field,updateChannelReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getChannelPage(Page<Channel> page, ChannelReq.SearchChannelReq searchChannelReq) {
        return Result.success(channelService.getChannelPage(page,searchChannelReq));
    }
}
