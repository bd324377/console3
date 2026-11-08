package com.console.activity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.activity.dto.req.ActivityReq;
import com.console.activity.entity.Activity;
import com.console.activity.service.ActivityService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Resource
    private ActivityService activityService;

    @PostMapping
    public Result addActivity(@RequestBody ActivityReq.AddActivityReq addActivityReq) {
        return activityService.addActivity(addActivityReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getActivityPage(Page<Activity> page) {
        return Result.success(activityService.getActivityPage(page));
    }
}
