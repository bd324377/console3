package com.console.api.controller;

import com.console.api.service.VipAPIService;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VipAPI {
    @Resource
    private VipAPIService vipAPIService;

    @PostMapping("/vipList")
    @PreAuthorize(ignore = true)
    public Result getVipList() {
        return Result.success(vipAPIService.getVipLevelList());
    }
}
