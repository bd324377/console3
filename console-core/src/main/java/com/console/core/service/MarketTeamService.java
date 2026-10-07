package com.console.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.MarketTeam;

import java.util.List;

/**
 * 营销团队服务接口
 */
public interface MarketTeamService extends IService<MarketTeam> {

    /**
     * 创建营销团队
     * @param marketTeam 营销团队信息
     * @return 是否成功
     */
    boolean createMarketTeam(MarketTeam marketTeam);

    /**
     * 更新营销团队
     * @param marketTeam 营销团队信息
     * @return 是否成功
     */
    boolean updateMarketTeam(MarketTeam marketTeam);

    /**
     * 删除营销团队
     * @param id 营销团队ID
     * @return 是否成功
     */
    boolean deleteMarketTeam(Integer id);

    /**
     * 根据ID获取营销团队信息
     * @param id 营销团队ID
     * @return 营销团队信息
     */
    MarketTeam getMarketTeamById(Integer id);
    MarketTeam getDefaultMarketTeam(Integer marketSiteId);

    /**
     * 获取所有营销团队列表
     * @return 营销团队列表
     */
    List<MarketTeam> getMarketTeamList();

    /**
     * 分页查询营销团队列表
     * @param page 分页参数
     * @param marketTeam 查询条件
     * @return 分页结果
     */
    IPage<MarketTeam> getMarketTeamPage(Page<MarketTeam> page, MarketTeam marketTeam);
}
