package com.console.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.MarketTeam;
import com.console.core.service.MarketTeamService;
import com.console.framework.domain.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 营销团队管理Controller
 */
@RestController
@RequestMapping("/marketTeam")
@RequiredArgsConstructor
public class MarketTeamController {

    private final MarketTeamService marketTeamService;

    /**
     * 创建营销团队
     * @param marketTeam 营销团队信息
     * @return 结果
     */
    @PostMapping
    public Result create(@RequestBody MarketTeam marketTeam) {
        boolean result = marketTeamService.createMarketTeam(marketTeam);
        return result ? Result.success() : Result.failed();
    }

    /**
     * 更新营销团队
     * @param marketTeam 营销团队信息
     * @return 结果
     */
    @PutMapping
    public Result update(@RequestBody MarketTeam marketTeam) {
        boolean result = marketTeamService.updateMarketTeam(marketTeam);
        return result ? Result.success() : Result.failed();
    }

    /**
     * 删除营销团队
     * @param id 营销团队ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean result = marketTeamService.deleteMarketTeam(id);
        return result ? Result.success() : Result.failed();
    }

    /**
     * 根据ID查询营销团队
     * @param id 营销团队ID
     * @return 营销团队信息
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        MarketTeam marketTeam = marketTeamService.getMarketTeamById(id);
        return Result.success(marketTeam);
    }

    /**
     * 查询所有营销团队列表
     * @return 营销团队列表
     */
    @GetMapping("/list")
    public Result list() {
        List<MarketTeam> list = marketTeamService.getMarketTeamList();
        return Result.success(list);
    }

    /**
     * 分页查询营销团队
     * @param current 当前页
     * @param size 每页大小
     * @param marketTeam 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Long current,
                       @RequestParam(defaultValue = "10") Long size,
                       MarketTeam marketTeam) {
        Page<MarketTeam> page = new Page<>(current, size);
        IPage<MarketTeam> result = marketTeamService.getMarketTeamPage(page, marketTeam);
        return Result.success(result);
    }

}
