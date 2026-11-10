package com.console.activity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.activity.entity.ActivityRule;
import com.console.activity.service.ActivityRuleService;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize(accountType = 1)
@RequestMapping("/activityRule")
public class ActivityRuleController {
    @Resource
    private ActivityRuleService activityRuleService;

    @PostMapping
    public Result addActivityRule(@RequestBody ActivityRule entity) {
        entity.setId(null);
        return null;//activityRuleService.add(service, entity, ActivityRule::getId) ? Result.success() : Result.failed();
    }

    @PutMapping("/{id}")
    public Result updateActivityRule(@PathVariable Integer id, @RequestBody ActivityRule entity) {
        entity.setId(id);
        return null;//activityCrudService.update(service, id, entity) ? Result.success() : Result.failed();
    }

    @DeleteMapping("/{id}")
    public Result delActivityRule(@PathVariable Integer id) {
        return null;//activityCrudService.delete(service, id) ? Result.success() : Result.failed();
    }

    @GetMapping("/{id}")
    public Result getActivityRule(@PathVariable Integer id) {
        return null;//Result.success(activityCrudService.getRequired(service, id));
    }

    @GetMapping
    public Result getActivityRulePage(Page<ActivityRule> page, @RequestParam(required = false) Integer activityId) {
        return null;//Result.success(service.lambdaQuery()
//                .eq(activityId != null, ActivityRule::getActivityId, activityId)
//                .orderByDesc(ActivityRule::getId)
//                .page(page));
    }
}
