package com.console.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.dto.req.MarketSiteReq;
import com.console.core.entity.MarketSite;

import java.util.List;

/**
 * 站点服务接口
 */
public interface MarketSiteService extends IService<MarketSite> {

    /**
     * 创建站点
     * @param createMarketSiteReq 站点信息
     * @return 是否成功
     */
    boolean createMarketSite(MarketSiteReq.CreateMarketSiteReq createMarketSiteReq);

    /**
     * 更新站点
     * @param updateMarketSiteReq 站点信息
     * @return 是否成功
     */
    boolean updateMarketSite(String part, MarketSiteReq.UpdateMarketSiteReq updateMarketSiteReq);

    /**
     * 删除站点
     * @param id 站点ID
     * @return 是否成功
     */
    boolean deleteMarketSite(Integer id);

    /**
     * 根据ID获取站点信息（带缓存）
     * @param siteId 站点ID
     * @return 站点信息
     */
    MarketSite getMarketSiteById(Integer siteId);
    MarketSiteBo getMarketSiteByDomain(String domain);

    /**
     * 获取所有站点列表（带缓存）
     * @return 站点列表
     */
    List<MarketSite> getMarketSiteList(Integer tenantId);

    /**
     * 分页查询站点列表
     * @param page 分页参数
     * @param marketSite 查询条件
     * @return 分页结果
     */
    IPage<MarketSite> getMarketSitePage(Page<MarketSite> page, MarketSite marketSite);

}
