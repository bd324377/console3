package com.console.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.model.dto.req.MarketSiteDomainReq;
import com.console.core.entity.MarketSiteDomain;

import java.util.List;

/**
 * 站点域名关系服务接口
 */
public interface MarketSiteDomainService extends IService<MarketSiteDomain> {

    /**
     * 创建站点域名关系
     * @param createMarketSiteDomainReq 站点域名关系信息
     * @return 是否成功
     */
    boolean createMarketSiteDomain(MarketSiteDomainReq.CreateMarketSiteDomainReq createMarketSiteDomainReq);

    /**
     * 更新站点域名关系
     * @param updateMarketSiteDomainReq 站点域名关系信息
     * @return 是否成功
     */
    boolean updateMarketSiteDomain(String fieldName,MarketSiteDomainReq.UpdateMarketSiteDomainReq updateMarketSiteDomainReq);

    /**
     * 删除站点域名关系
     * @param id 站点域名关系ID
     * @return 是否成功
     */
    boolean deleteMarketSiteDomain(Integer id);

    /**
     * 根据ID获取站点域名关系信息
     * @param id 站点域名关系ID
     * @return 站点域名关系信息
     */
    MarketSiteDomain getMarketSiteDomainById(Integer id);

    /**
     * 获取所有站点域名关系列表
     * @return 站点域名关系列表
     */
    List<MarketSiteDomain> getMarketSiteDomainList();

    /**
     * 分页查询站点域名关系列表
     * @param page 分页参数
     * @param marketSiteDomain 查询条件
     * @return 分页结果
     */
    IPage<MarketSiteDomain> getMarketSiteDomainPage(Page<MarketSiteDomain> page, MarketSiteDomain marketSiteDomain);

    /**
     * 根据站点ID获取域名列表
     * @param siteId 站点ID
     * @return 域名列表
     */
    List<MarketSiteDomain> getDomainsBySiteId(Integer siteId);

    /**
     * 根据状态获取域名列表
     * @param status 域名状态
     * @return 域名列表
     */
    List<MarketSiteDomain> getDomainsByStatus(Integer status);
    MarketSiteDomain getMarketSiteByDomain(String domain);
}
