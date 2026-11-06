package com.console.api.controller;

import com.console.api.dto.req.UserReq;
import com.console.api.service.UserAPIService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAPI {
    @Resource
    private UserAPIService userAPIService;

    @PostMapping("/selfInfo")
    public Result getSelfInfo() {
        return Result.success(userAPIService.selfInfo());
    }

    @PostMapping("/bindPhone")
    public Result bindPhone(@RequestBody @Validated UserReq.BindPhoneReq bindPhoneReq) {
        return userAPIService.bindPhone(bindPhoneReq) ? Result.success() : Result.failed(500);
    }

    @PostMapping("/bindEmail")
    public Result bindEmail(@RequestBody @Validated UserReq.BindEmailReq bindEmailReq) {
        return userAPIService.bindEmail(bindEmailReq) ? Result.success() : Result.failed(500);
    }

    /**
     * 更新密码
     */
    @PostMapping("/updateLoginPwd")
    public Result updateLoginPwd(@RequestBody @Validated UserReq.UpdatePwdReq updatePwdReq) {
        return userAPIService.updateLoginPwd(updatePwdReq) ? Result.success() : Result.failed(500);
    }

    /**
     * 更新用户信息
     * @param updateUserInfoReq 更新信息
     */
    @PostMapping("/updateSelfInfo")
    public Result updateSelfInfo(@RequestBody UserReq.UpdateUserInfoReq updateUserInfoReq) {
        return userAPIService.updateSelfInfo(updateUserInfoReq) ? Result.success() : Result.failed(2);
    }

//    /**
//     * 获取系统头像列表
//     */
//    @PostMapping("/avatarOptions/{sex}")
//    public Result getAvatarPicUrlOptions(@PathVariable("sex") Integer sex) {
//        return Result.success(userAPIService.getAvatarList(sex));
//    }
}
