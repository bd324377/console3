package com.console.api.controller;

import com.console.api.dto.req.LogSearchReq;
import com.console.api.dto.req.ReceiveRewardReq;
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

    /** 领取宝箱奖励 **/
    @PostMapping("/receiveInviteReward")
    public Result receiveInviteReward(@RequestBody @Validated ReceiveRewardReq receiveInviteRewardDTO) {
        return Result.success(activityAPIService.receiveInviteReward(receiveInviteRewardDTO));
    }

    /** 领取升级奖励 **/
    @PostMapping("/receiveVipUpgradeReward")
    public Result receiveVipUpgradeReward(@RequestBody @Validated ReceiveRewardReq receiveInviteRewardDTO) {
        return Result.success(activityAPIService.receiveActivityReward(receiveInviteRewardDTO));
    }

    /** 领取宝箱奖励 **/
    @PostMapping("/checkIntroduction")
    @PreAuthorize(ignore = true)
    public Result checkIntroduction() {
        return Result.success(activityAPIService.checkIntroduction());
    }

    /** 签到 **/
    @PostMapping("/checkIn")
    public Object checkIn() {
        return activityAPIService.checkIn();
    }

    /** 兑换码  **/
    @PostMapping("/redeeming/{redeemCode}")
    public CompletableFuture<Object> redeeming(@PathVariable String redeemCode) {
        return activityAPIService.redeeming(redeemCode);
    }

    /** 获取待领取奖励列表 **/
    @PostMapping("/claimableRewardsList")
    public Result getClaimableRewardsList() {
        return Result.success(activityAPIService.getClaimableRewardsList());
    }

    /** 获取活动奖励日志 **/
    @PostMapping("/receiveRewardLogs")
    public Result getReceiveRewardLogs(@RequestBody LogSearchReq logSearchReq) {
        return Result.success(activityAPIService.getReceiveRewardLogs(logSearchReq));
    }
}
