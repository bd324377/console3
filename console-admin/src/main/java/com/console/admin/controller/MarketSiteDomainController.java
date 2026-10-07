package com.console.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.model.dto.req.MarketSiteDomainReq;
import com.console.core.entity.MarketSiteDomain;
import com.console.core.service.MarketSiteDomainService;
import com.console.framework.domain.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站点域名关系管理Controller
 */
@RestController
@RequestMapping("/marketSiteDomain")
@RequiredArgsConstructor
public class MarketSiteDomainController {

    private final MarketSiteDomainService marketSiteDomainService;

    /**
     * 创建站点域名关系
     * @param createMarketSiteDomainReq 站点域名关系信息
     * @return 结果
     */
    @PostMapping
    public Result create(@RequestBody @Validated MarketSiteDomainReq.CreateMarketSiteDomainReq createMarketSiteDomainReq) {
        return marketSiteDomainService.createMarketSiteDomain(createMarketSiteDomainReq) ? Result.success() : Result.failed();
    }

    /**
     * 更新站点域名关系
     * @param updateMarketSiteDomainReq 站点域名关系信息
     * @return 结果
     */
    @PutMapping("/{fieldName}")
    public Result update(@PathVariable String fieldName,@RequestBody MarketSiteDomainReq.UpdateMarketSiteDomainReq updateMarketSiteDomainReq) {
        return marketSiteDomainService.updateMarketSiteDomain(fieldName,updateMarketSiteDomainReq) ? Result.success() : Result.failed();
    }

    /**
     * 删除站点域名关系
     * @param id 站点域名关系ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean result = marketSiteDomainService.deleteMarketSiteDomain(id);
        return result ? Result.success() : Result.failed();
    }

    /**
     * 根据ID查询站点域名关系
     * @param id 站点域名关系ID
     * @return 站点域名关系信息
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        MarketSiteDomain marketSiteDomain = marketSiteDomainService.getMarketSiteDomainById(id);
        return Result.success(marketSiteDomain);
    }

    /**
     * 查询所有站点域名关系列表
     * @return 站点域名关系列表
     */
    @GetMapping("/list")
    public Result list() {
        List<MarketSiteDomain> list = marketSiteDomainService.getMarketSiteDomainList();
        return Result.success(list);
    }

    /**
     * 分页查询站点域名关系
     * @param current 当前页
     * @param size 每页大小
     * @param marketSiteDomain 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Long current,
                       @RequestParam(defaultValue = "10") Long size,
                       MarketSiteDomain marketSiteDomain) {
        Page<MarketSiteDomain> page = new Page<>(current, size);
        IPage<MarketSiteDomain> result = marketSiteDomainService.getMarketSiteDomainPage(page, marketSiteDomain);
        return Result.success(result);
    }

    /**
     * 根据站点ID查询域名列表
     * @param siteId 站点ID
     * @return 域名列表
     */
    @GetMapping("/site/{siteId}")
    public Result getBySiteId(@PathVariable Integer siteId) {
        List<MarketSiteDomain> list = marketSiteDomainService.getDomainsBySiteId(siteId);
        return Result.success(list);
    }

    /**
     * 根据状态查询域名列表
     * @param status 域名状态
     * @return 域名列表
     */
    @GetMapping("/status/{status}")
    public Result getByStatus(@PathVariable Integer status) {
        List<MarketSiteDomain> list = marketSiteDomainService.getDomainsByStatus(status);
        return Result.success(list);
    }
}
