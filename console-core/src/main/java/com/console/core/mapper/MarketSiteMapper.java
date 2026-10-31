package com.console.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.MarketSite;
import com.console.core.model.req.MarketSiteReq;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点Mapper接口
 */
@Mapper
public interface MarketSiteMapper extends BaseMapper<MarketSite> {
    IPage<MarketSite> selectMarketSitePage(Page<MarketSite> page, MarketSiteReq.SearchMarketSiteReq searchMarketSiteReq);
}
