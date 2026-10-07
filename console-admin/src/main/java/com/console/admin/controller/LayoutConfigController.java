package com.console.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.admin.dto.req.LayoutConfigReq;
import com.console.admin.entity.LayoutConfig;
import com.console.admin.service.LayoutConfigService;
import com.console.framework.domain.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 布局配置管理Controller
 */
@RestController
@RequestMapping("/layoutConfig")
@RequiredArgsConstructor
public class LayoutConfigController {

    private final LayoutConfigService layoutConfigService;

    /**
     * 创建布局配置
     * @param layoutConfig 布局配置信息
     * @return 结果
     */
    @PostMapping
    public Result create(@RequestBody LayoutConfigReq.CreateLayoutConfigReq layoutConfig) {
        return layoutConfigService.createLayoutConfig(layoutConfig) ? Result.success() : Result.failed();
    }

    /**
     * 更新布局配置
     * @param layoutConfig 布局配置信息
     * @return 结果
     */
    @PutMapping("/{part}")
    public Result update(@PathVariable String part, @RequestBody LayoutConfig layoutConfig) {
        boolean result = layoutConfigService.updateLayoutConfig(layoutConfig);
        return result ? Result.success() : Result.failed();
    }

    /**
     * 删除布局配置
     * @param id 布局配置ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean result = layoutConfigService.deleteLayoutConfig(id);
        return result ? Result.success() : Result.failed();
    }

    /**
     * 根据ID查询布局配置
     * @param id 布局配置ID
     * @return 布局配置信息
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        LayoutConfig layoutConfig = layoutConfigService.getLayoutConfigById(id);
        return Result.success(layoutConfig);
    }

    /**
     * 查询所有布局配置列表
     * @return 布局配置列表
     */
    @GetMapping("/list")
    public Result list() {
        List<LayoutConfig> list = layoutConfigService.getLayoutConfigList();
        return Result.success(list);
    }

    /**
     * 分页查询布局配置
     * @param current 当前页
     * @param size 每页大小
     * @param layoutConfig 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1")  Long current,
                       @RequestParam(defaultValue = "10") Long size,
                       LayoutConfig layoutConfig) {
        Page<LayoutConfig> page = new Page<>(current, size);
        IPage<LayoutConfig> result = layoutConfigService.getLayoutConfigPage(page, layoutConfig);
        return Result.success(result);
    }

    /**
     * 根据布局类型查询布局配置列表
     * @param layoutType 布局类型
     * @return 布局配置列表
     */
    @GetMapping("/type/{layoutType}")
    public Result getByType(@PathVariable Integer layoutType) {
        List<LayoutConfig> list = layoutConfigService.getLayoutConfigsByType(layoutType);
        return Result.success(list);
    }
}
