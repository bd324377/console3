package com.console.api.controller;

import com.console.api.service.UserAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAPI {
    @Resource
    private UserAPIService userAPIService;

    @PostMapping("/selfInfo")
    public Result getSelfInfo() {
        return Result.success(userAPIService.selfInfo());
    }
}
