package com.console.core.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.model.dto.req.TenantReq;
import com.console.core.entity.Tenant;
import com.console.core.service.TenantService;
import com.console.framework.domain.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理Controller
 */
@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 创建租户
     * @param  createTenantReq 租户信息
     * @return 结果
     */
    @PostMapping
    public Result create(@RequestBody TenantReq.CreateTenantReq createTenantReq) {
        return tenantService.createTenant(createTenantReq) ? Result.success() : Result.failed();
    }

    /**
     * 更新租户
     * @param updateTenantReq 租户信息
     * @return 结果
     */
    @PutMapping("/{part}")
    public Result update(@PathVariable String part,@RequestBody @Validated TenantReq.UpdateTenantReq updateTenantReq) {
        return tenantService.updateTenant(part,updateTenantReq) ? Result.success() : Result.failed();
    }

    /**
     * 根据ID查询租户
     * @param id 租户ID
     * @return 租户信息
     */
    @GetMapping("/{id}")
    public Result getTenantDetailsById(@PathVariable Integer id) {
        return Result.success(tenantService.getTenantDetailsById(id));
    }

    /**
     * 查询所有租户列表
     * @return 租户列表
     */
    @GetMapping("/list")
    public Result list() {
        List<Tenant> list = tenantService.getTenantList();
        return Result.success(list);
    }

    /**
     * 分页查询租户
     * @param current 当前页
     * @param size 每页大小
     * @param tenant 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Long current,
                       @RequestParam(defaultValue = "10") Long size,
                       Tenant tenant) {
        Page<Tenant> page = new Page<>(current, size);
        IPage<Tenant> result = tenantService.getTenantPage(page, tenant);
        return Result.success(result);
    }
}
