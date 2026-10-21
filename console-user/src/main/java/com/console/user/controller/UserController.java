package com.console.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.framework.domain.Result;
import com.console.user.entity.User;
import com.console.user.model.dto.req.UserReq;
import com.console.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping
    public Result batchCreateUsers(@RequestBody UserReq.CreateUserReq createUserReq) {
        return Result.success(userService.batchCreateUsers(createUserReq));
    }

    @PutMapping("/{field}")
    public Result updateUser(@PathVariable String field, @RequestBody @Validated UserReq.UpdateUserReq updateUserReq) {
        return userService.updateUser(field, updateUserReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getUserPage(Page<User> userPage, UserReq.SearchUserReq searchUserReq) {
        return Result.success();
    }
}
