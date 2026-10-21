package com.console.api.controller;

import com.console.api.dto.req.LoginReq;
import com.console.api.dto.req.RefreshTokenReq;
import com.console.api.service.LoginAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginAPI {
    @Resource
    private LoginAPIService loginApiService;

    @PostMapping("/login")
    public Result login(@RequestBody @Validated LoginReq loginReq) {
        return Result.success(loginApiService.login(loginReq));
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody @Validated RefreshTokenReq refreshTokenReq) {
        return Result.success(loginApiService.refreshUserToken(refreshTokenReq.getRefreshToken()));
    }
}
