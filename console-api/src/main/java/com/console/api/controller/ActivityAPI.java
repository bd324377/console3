package com.console.api.controller;

import com.console.api.service.ActivityAPIService;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
