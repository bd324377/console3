package com.console.admin.controller;

import com.console.admin.dto.req.MenuReq;
import com.console.admin.service.MenuService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    @PostMapping
    public Result createMenu(@RequestBody MenuReq.CreateMenuReq createMenuReq) {
        return menuService.createMenu(createMenuReq) ? Result.success() : Result.failed();
    }

    @DeleteMapping("/{menuId}")
    public Result delMenuById(@PathVariable Long menuId) {
        return menuService.delMenuById(menuId) ? Result.success() : Result.failed();
    }

    @PutMapping("/{part}")
    public Result updateMenu(@PathVariable String part,@RequestBody MenuReq.UpdateMenuReq updateMenuReq) {
        return menuService.updateMenu(part,updateMenuReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getMenuTree() {
        return Result.success(menuService.getMenuTree());
    }
}
