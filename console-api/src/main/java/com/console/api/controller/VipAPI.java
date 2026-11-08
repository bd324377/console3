package com.console.api.controller;

import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VipAPI {

    @PostMapping("/vipList")
    @PreAuthorize(ignore = true)
    public Result getVipList() {
        return Result.success();
    }
}
