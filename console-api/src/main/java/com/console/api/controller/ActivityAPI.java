package com.console.api.controller;

import com.console.api.dto.req.ReceiveInviteRewardReq;
import com.console.api.service.ActivityAPIService;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class ActivityAPI {
    @Resource
    private ActivityAPIService activityAPIService;

    @PostMapping("/activityList")
    @PreAuthorize(ignore = true)
    public Result getActivityList() {
        return Result.success(activityAPIService.getActivityList());
    }

    /** 邀新奖励 **/
    @PostMapping("/inviteNewUserRewards")
    @PreAuthorize(ignore = true)
    public Result getInviteNewUserRewards() {
        return Result.success(activityAPIService.getInviteNewUserRewards());
    }

    /**
     * 领取宝箱奖励
     * @param receiveInviteRewardDTO 领取参数
     */
    @PostMapping("/receiveInviteReward")
    public Result receiveInviteReward(@RequestBody @Validated ReceiveInviteRewardReq receiveInviteRewardDTO) {
        return Result.success(activityAPIService.receiveInviteReward(receiveInviteRewardDTO));
    }

    @PostMapping("/checkIntroduction")
    @PreAuthorize(ignore = true)
    public Result checkIntroduction() {
        return Result.success(activityAPIService.checkIntroduction());
    }

    @PostMapping("/checkIn")
    public Object checkIn() {
        return activityAPIService.checkIn();
    }

    @PostMapping("/redeeming/{redeemCode}")
    public CompletableFuture<Object> redeeming(@PathVariable String redeemCode) {
        return activityAPIService.redeeming(redeemCode);
    }
}
