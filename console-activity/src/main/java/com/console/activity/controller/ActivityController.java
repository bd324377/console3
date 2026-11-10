package com.console.activity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.activity.dto.req.ActivityReq;
import com.console.activity.entity.Activity;
import com.console.activity.service.ActivityService;
import com.console.framework.domain.Result;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize(accountType = 1)
@RequestMapping("/activity")
public class ActivityController {
    @Resource
    private ActivityService activityService;

    @PostMapping
    public Result addActivity(@RequestBody ActivityReq.AddActivityReq addActivityReq) {
        return activityService.addActivity(addActivityReq) ? Result.success() : Result.failed();
    }

    @DeleteMapping("/{activityId}")
    public Result delActivity(@PathVariable Integer activityId) {
        return activityService.delActivity(activityId) ? Result.success() : Result.failed();
    }

    @PutMapping("/{field}")
    public Result updateActivity(@PathVariable String field, @RequestBody @Validated ActivityReq.UpdateActivityReq updateActivityReq) {
        return activityService.updateActivity(field,updateActivityReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getActivityPage(Page<Activity> page) {
        return Result.success(activityService.getActivityPage(page));
    }

    @GetMapping("/{activityId}")
    public Result getActivity(@PathVariable Integer activityId) {
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }
        return Result.success(activity);
    }
}
