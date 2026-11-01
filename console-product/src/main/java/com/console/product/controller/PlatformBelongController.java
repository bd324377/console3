package com.console.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.framework.domain.Result;
import com.console.product.entity.PlatformBelong;
import com.console.product.service.PlatformBelongService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/platformBelong")
public class PlatformBelongController {
    @Resource
    private PlatformBelongService platformBelongService;

    @PutMapping
    public Result updatePlatformBelongStatus(@RequestBody @Validated PlatformBelong platformBelong) {
        return platformBelongService.updatePlatformBelongStatus(platformBelong) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getPlatformBelongPage(Page<PlatformBelong> page) {
        return Result.success(platformBelongService.page(page));
    }
}
