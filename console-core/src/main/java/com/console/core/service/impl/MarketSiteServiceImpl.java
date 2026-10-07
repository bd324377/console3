package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.MarketTeam;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.dto.req.MarketSiteReq;
import com.console.core.entity.MarketSite;
import com.console.core.entity.MarketSiteDomain;
import com.console.core.entity.Tenant;
import com.console.core.mapper.MarketSiteMapper;
import com.console.core.service.MarketSiteDomainService;
import com.console.core.service.MarketSiteService;
import com.console.core.service.MarketTeamService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 站点服务实现类
 */
@Slf4j
@Service
public class MarketSiteServiceImpl extends BaseServiceImpl<MarketSiteMapper, MarketSite> implements MarketSiteService {
    @Resource
    private MarketSiteDomainService marketSiteDomainService;
    @Resource
    private MarketTeamService marketTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.MARKET_SITE_LIST, allEntries = true)
    public boolean createMarketSite(MarketSiteReq.CreateMarketSiteReq createMarketSiteReq) {
        MarketSite marketSite = new MarketSite();
        BeanUtils.copyProperties(createMarketSiteReq,marketSite);
        return this.save(marketSite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstants.MARKET_SITE, CacheConstants.MARKET_SITE_LIST},allEntries = true)
    public boolean updateMarketSite(String part, MarketSiteReq.UpdateMarketSiteReq updateMarketSiteReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getUser(RequestBackendUser.class);
        DynamicTableNameHandler.setTenantId(requestBackendUser.getTenantId());
        QueryWrapper<MarketSite> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(part))
                .lambda().eq(MarketSite::getId,updateMarketSiteReq.getSiteId());
        MarketSite marketSite = this.getOne(queryWrapper);
        updateMarketSiteReq.buildUpdateParam(part,marketSite,requestBackendUser);
        if (StringUtils.hasText(updateMarketSiteReq.getErrorDesc())) {
            throw new BusinessException(500,updateMarketSiteReq.getErrorDesc());
        }
        if (this.update(updateMarketSiteReq.getUpdateWrapper())) {//更新成功，记录操作过程
            adminProcMapper.insert(updateMarketSiteReq.getAdminProc());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstants.MARKET_SITE, CacheConstants.MARKET_SITE_LIST}, allEntries = true)
    public boolean deleteMarketSite(Integer id) {
        return this.removeById(id);
    }

    @Override
    @Cacheable( value = CacheConstants.MARKET_SITE, key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #siteId")
    public MarketSite getMarketSiteById(Integer siteId) {
        QueryWrapper<MarketSite> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,tenant_id,site_name,layout_id,order_mode," +
                        "manual_deposit_limit,manual_deposited_amt,withdrawal_limit," +
                        "recharge_rate,order_rate,status")
                .lambda().eq(MarketSite::getId,siteId);
        return this.getById(siteId);
    }

    @Cacheable( value = CacheConstants.MARKET_SITE, key = "#domain")
    public MarketSiteBo getMarketSiteByDomain(String domain) {
        Tenant tenant = tenantService.getTenantByDomain(domain);
        if (tenant != null) {
            DynamicTableNameHandler.setTenantId(tenant.getId());
            MarketSiteDomain marketSiteDomain = marketSiteDomainService.getMarketSiteByDomain(domain);
            if (marketSiteDomain != null) {
                MarketSite marketSite = getMarketSiteById(marketSiteDomain.getMarketSiteId());
                if (marketSite != null) {
                    MarketTeam marketTeam = marketTeamService.getDefaultMarketTeam(marketSite.getId());
                    if (marketTeam != null) {
                        return new MarketSiteBo(tenant,marketSite,marketTeam);
                    }
                }
            }
        }
        return null;
    }

    @Override
    @Cacheable( value = CacheConstants.MARKET_SITE_LIST, key   = "#tenantId + ':ALL'")
    public List<MarketSite> getMarketSiteList(Integer tenantId) {
        DynamicTableNameHandler.setTenantId(tenantId);
        QueryWrapper<MarketSite> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,site_name,layout_id").lambda().eq(MarketSite::getStatus,1);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<MarketSite> getMarketSitePage(Page<MarketSite> page, MarketSite marketSite) {
        QueryWrapper<MarketSite> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(marketSite.getTenantId() != null, MarketSite::getTenantId, marketSite.getTenantId())
                .like(StringUtils.hasText(marketSite.getSiteName()), MarketSite::getSiteName, marketSite.getSiteName())
                .eq(marketSite.getStatus() != null, MarketSite::getStatus, marketSite.getStatus())
                .eq(marketSite.getLayoutId() != null, MarketSite::getLayoutId, marketSite.getLayoutId())
                .orderByDesc(MarketSite::getCreateTime);
        return this.page(page, queryWrapper);
    }

//    @Override
//    public MarketSite getMarketSiteByDomain() {
//
//    }
}
