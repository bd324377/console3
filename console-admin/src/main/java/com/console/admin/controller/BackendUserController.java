package com.console.admin.controller;

import com.console.admin.dto.req.BackendUserReq;
import com.console.admin.service.BackendUserService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backendUser")
public class BackendUserController {
    @Resource
    private BackendUserService backendUserService;

    @PostMapping("/login")
    public Result login(@RequestBody @Validated BackendUserReq.LoginReq loginReq) {
        return Result.success(backendUserService.login(loginReq));
    }

    @PutMapping("/refreshToken")
    public Result refreshBackendUserToken(@RequestBody @Validated BackendUserReq.RefreshTokenReq refreshTokenReq) {
        return Result.success(backendUserService.refreshBackendUserToken(refreshTokenReq.getRefreshToken()));
    }

    /**
     * 新增管理员用户
     *
     * @param createBackendUserReq 管理员用户信息
     */
    @PostMapping("/admin")
    public Result createAdminUser(@RequestBody @Validated BackendUserReq.CreateBackendUserReq createBackendUserReq) {
        return backendUserService.createAdminUser(createBackendUserReq) ? Result.success() : Result.failed();
    }

    /**
     * 新增分销渠道后台用户
     * @param createBackendUserReq 新增信息
     */
    @PostMapping("/marketer")
    public Result createMarketer(@RequestBody @Validated BackendUserReq.CreateBackendUserReq createBackendUserReq) {
        return backendUserService.createMarketer(createBackendUserReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getAdminPage(BackendUserReq.SearchBackendUserReq searchBackendUserReq) {
        return Result.success(backendUserService.getAdminPage(searchBackendUserReq));
    }
}
