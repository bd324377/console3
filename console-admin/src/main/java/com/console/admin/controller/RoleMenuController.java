package com.console.admin.controller;

import com.console.admin.dto.req.RoleMenuReq;
import com.console.admin.service.RoleMenuService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roleMenu")
public class RoleMenuController {
    @Resource
    private RoleMenuService roleMenuService;

    @GetMapping("/roles")
    public Result getRoleList() {
        return Result.success(roleMenuService.getRoleList());
    }

    @PostMapping
    public Result addOrUpdateRoleMenus(@RequestBody @Validated RoleMenuReq roleMenuReq) {
        return roleMenuService.addOrUpdateRoleMenus(roleMenuReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getMenusByRequestUser() {
        return Result.success(roleMenuService.getMenusByRequestUser());
    }

    @GetMapping("/forUpdate/{roleId}")
    public Result getMenusByRoleIdForUpdate(@PathVariable Integer roleId) {
        return Result.success(roleMenuService.getMenusByRoleIdForUpdate(roleId));
    }
}
