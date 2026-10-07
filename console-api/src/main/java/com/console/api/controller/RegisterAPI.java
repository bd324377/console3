package com.console.api.controller;

import com.console.api.dto.req.RegReq;
import com.console.api.service.RegApiService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterAPI {
    @Resource
    private RegApiService regApiService;

    @PostMapping("/register")
    public Result register(@RequestBody @Validated RegReq regReq) {
        return Result.success(regApiService.register(regReq));
    }
}
