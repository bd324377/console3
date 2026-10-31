package com.console.core.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.TenantDomain;
import com.console.core.service.TenantDomainService;
import com.console.framework.constants.ValidationGroup;
import com.console.framework.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenantDomain")
public class TenantDomainController {
    @Resource
    private TenantDomainService tenantDomainService;

    @PostMapping
    public Result addTenantDomain(@RequestBody @Validated(ValidationGroup.insert.class) TenantDomain tenantDomain) {
        tenantDomainService.addTenantDomain(tenantDomain);
        return Result.success();
    }

    @PutMapping
    public Result updateTenantDomain(@RequestBody @Validated(ValidationGroup.update.class) TenantDomain tenantDomain) {
        tenantDomainService.updateTenantDomain(tenantDomain);
        return Result.success();
    }

    @GetMapping
    public Result getTenantPageByTenantId(Page<TenantDomain> page, TenantDomain param) {
        return Result.success(tenantDomainService.getTenantPageByTenantId(page,param));
    }
}
