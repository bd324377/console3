package com.console.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.model.req.ShortcutIconReq;
import com.console.admin.entity.ShortcutIcon;
import com.console.admin.service.ShortcutIconService;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shortcutIcon")
public class ShortcutIconController {
    @Resource
    private ShortcutIconService shortcutIconService;

    @PostMapping
    public Result addShortcutIcon(@RequestBody @Validated ShortcutIconReq.CreateShortIconReq createShortIconReq) {
        return shortcutIconService.addShortcutIcon(createShortIconReq) ? Result.success() : Result.failed();
    }

    @DeleteMapping
    public Result delShortcutIcon(@RequestBody @Validated ShortcutIconReq.DelShortIconReq delShortIconReq) {
        return shortcutIconService.delShortcutIcon(delShortIconReq) ? Result.success() : Result.failed();
    }

    @PutMapping("/{part}")
    public Result updateCustomerCare(@PathVariable(value = "part") String part, @RequestBody @Validated ShortcutIconReq.UpdateShortIconReq updateShortIconReq) {
        return shortcutIconService.updateShortcutIcon(part, updateShortIconReq) ? Result.success() : Result.failed();
    }

    @GetMapping
    public Result getCustomerCarePage(Page<ShortcutIcon> page, ShortcutIconReq.SearchShortIconReq searchShortIconReq) {
        return Result.success(shortcutIconService.getShortcutIconPage(page, searchShortIconReq));
    }
}
