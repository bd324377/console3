package com.console.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.model.dto.req.MarketSiteReq;
import com.console.core.entity.MarketSite;
import com.console.core.service.MarketSiteService;
import com.console.framework.domain.Result;
import com.console.framework.utils.TenantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站点管理Controller
 */
@RestController
@RequestMapping("/marketSite")
@RequiredArgsConstructor
public class MarketSiteController {

    private final MarketSiteService marketSiteService;

    /**
     * 创建站点
     * @return 结果
     */
    @PostMapping
    public Result create(@RequestBody MarketSiteReq.CreateMarketSiteReq createMarketSiteReq) {
        return marketSiteService.createMarketSite(createMarketSiteReq) ? Result.success() : Result.failed();
    }

    /**
     * 更新站点
     * @param updateMarketSiteReq 待修改站点信息
     * @return 结果
     */
    @PutMapping("/{part}")
    public Result update(@PathVariable String part,@RequestBody MarketSiteReq.UpdateMarketSiteReq updateMarketSiteReq) {
        return marketSiteService.updateMarketSite(part,updateMarketSiteReq) ? Result.success() : Result.failed();
    }


    /**
     * 查询所有站点列表
     * @return 站点列表
     */
    @GetMapping("/list")
    public Result list() {
        List<MarketSite> list = marketSiteService.getMarketSiteList(TenantUtils.getTenantId());
        return Result.success(list);
    }

    /**
     * 分页查询站点
     * @param current 当前页
     * @param size 每页大小
     * @param marketSite 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Long current,
                       @RequestParam(defaultValue = "10") Long size,
                       MarketSite marketSite) {
        Page<MarketSite> page = new Page<>(current, size);
        IPage<MarketSite> result = marketSiteService.getMarketSitePage(page, marketSite);
        return Result.success(result);
    }
}
