package com.console.api.controller;

import com.console.api.dto.req.LoginReq;
import com.console.api.dto.req.RefreshTokenReq;
import com.console.api.service.LoginApiService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginAPI {
    @Resource
    private LoginApiService loginApiService;

    @PostMapping("/login")
    public Result login(@RequestBody @Validated LoginReq loginReq) {
        return Result.success();
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody @Validated RefreshTokenReq refreshTokenReq) {
        return Result.success(loginApiService.refreshUserToken(refreshTokenReq.getRefreshToken()));
    }
}
