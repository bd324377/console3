package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.*;
import com.console.core.mapper.MarketerMapper;
import com.console.core.merger.AdminProcMerger;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.model.dto.req.MarketSiteReq;
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
    private AdminProcMerger adminProcMerger;
    @Resource
    private MarketSiteDomainService marketSiteDomainService;
    @Resource
    private MarketTeamService marketTeamService;
    @Resource
    private MarketerMapper marketerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.MARKET_SITE_LIST, allEntries = true)
    public boolean createMarketSite(MarketSiteReq.CreateMarketSiteReq createMarketSiteReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        MarketSite marketSite = new MarketSite();
        BeanUtils.copyProperties(createMarketSiteReq,marketSite);
        if (this.save(marketSite)) {//新建站点
            //创建该站点官方（默认）渠道
            MarketTeam marketTeam = new MarketTeam();
            marketTeam.setMarketSiteId(marketSite.getId());
            marketTeam.setTeamName("官方渠道");
            marketTeam.setIdDefaultTeam(1);
            marketTeam.setLeaderMarketerId(2);
            marketTeam.setOrderMode(marketSite.getOrderMode());
            marketTeamService.save(marketTeam);

            Marketer marketer = new Marketer();//创建默认营业员
            marketer.setMarketTeamId(marketTeam.getId());
            marketer.setMarketSiteId(marketSite.getId());
            marketer.setMarketerId(2);
            marketer.setIsLeader(1);
            marketer.setOrderMode(marketTeam.getOrderMode());
            marketerMapper.insert(marketer);

            AdminProc createSiteAdminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.MARKET_SITE,
                    AdminProc.ProcType.ADD,marketSite.getId(),null,marketSite);
            AdminProc createTeamAdminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.MARKET_TEAM,
                    AdminProc.ProcType.ADD, marketTeam.getId(),null,marketTeam);
            AdminProc createMarketerAdminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.MARKETER,
                    AdminProc.ProcType.ADD, marketer.getId(),null,marketer);
            adminProcMerger.submit(createSiteAdminProc);
            adminProcMerger.submit(createTeamAdminProc);
            adminProcMerger.submit(createMarketerAdminProc);
            return true;
        }
        return false;
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
    public IPage<MarketSite> getMarketSitePage(Page<MarketSite> page, MarketSiteReq.SearchMarketSiteReq searchMarketSiteReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        if (requestBackendUser.getAccountType() == 0) {//管理员用户
            QueryWrapper<MarketSite> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .like(StringUtils.hasText(searchMarketSiteReq.getSiteName()), MarketSite::getSiteName, searchMarketSiteReq.getSiteName())
                    .eq(searchMarketSiteReq.getStatus() != null, MarketSite::getStatus, searchMarketSiteReq.getStatus())
                    .eq(searchMarketSiteReq.getLayoutId() != null, MarketSite::getLayoutId, searchMarketSiteReq.getLayoutId())
                    .orderByDesc(MarketSite::getCreateTime);
            return this.page(page, queryWrapper);
        } else {//分销用户
            searchMarketSiteReq.setMarketerId(requestBackendUser.getId());
            return this.baseMapper.selectMarketSitePage(page, searchMarketSiteReq);
        }
    }

//    @Override
//    public MarketSite getMarketSiteByDomain() {
//
//    }
}
