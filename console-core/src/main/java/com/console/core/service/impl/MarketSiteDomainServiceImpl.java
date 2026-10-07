package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.model.dto.req.MarketSiteDomainReq;
import com.console.core.entity.MarketSiteDomain;
import com.console.core.mapper.MarketSiteDomainMapper;
import com.console.core.service.MarketSiteDomainService;
import com.console.core.entity.Tenant;
import com.console.core.service.TenantService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 站点域名关系服务实现类
 */
@Slf4j
@Service
public class MarketSiteDomainServiceImpl extends BaseServiceImpl<MarketSiteDomainMapper, MarketSiteDomain> implements MarketSiteDomainService {
    @Resource
    private TenantService tenantService;

    @Override
    @CacheEvict(value = CacheConstants.MARKET_SITE_DOMAIN_LIST)
    public boolean createMarketSiteDomain(MarketSiteDomainReq.CreateMarketSiteDomainReq createMarketSiteDomainReq) {
        Tenant tenant = getTenantByDomain();
        if (tenant == null) {
            throw new BusinessException(500,"当前域名未绑定租户，请确认后重试");
        }
        if (ObjectUtils.isEmpty(tenant.getDomains()) || !tenant.getDomains().contains(createMarketSiteDomainReq.getSiteDomain())) {
            throw new BusinessException(500,"添加域名未绑定到当前租户中");
        }
        MarketSiteDomain marketSiteDomain = new MarketSiteDomain();
        marketSiteDomain.setMarketSiteId(createMarketSiteDomainReq.getSiteId());
        marketSiteDomain.setSiteDomain(createMarketSiteDomainReq.getSiteDomain());
        return this.save(marketSiteDomain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMarketSiteDomain(String fieldName,MarketSiteDomainReq.UpdateMarketSiteDomainReq updateMarketSiteDomainReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getUser(RequestBackendUser.class);
        QueryWrapper<MarketSiteDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", FieldUtils.convertToUnderscore(fieldName)).lambda().eq(MarketSiteDomain::getId,updateMarketSiteDomainReq.getId());
        MarketSiteDomain marketSiteDomain = this.getOne(queryWrapper);
        updateMarketSiteDomainReq.buildUpdateParam(fieldName,marketSiteDomain,requestBackendUser);
        if (StringUtils.hasText(updateMarketSiteDomainReq.getErrorDesc())) {
            throw new BusinessException(500,updateMarketSiteDomainReq.getErrorDesc());
        }
        if (this.update(updateMarketSiteDomainReq.getUpdateWrapper())) {//更新成功，记录操作过程
            DynamicTableNameHandler.setTenantId(requestBackendUser.getTenantId());
            adminProcMapper.insert(updateMarketSiteDomainReq.getAdminProc());
            return true;
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMarketSiteDomain(Integer id) {
        return this.removeById(id);
    }

    @Override
    public MarketSiteDomain getMarketSiteDomainById(Integer id) {
        return this.getById(id);
    }

    @Override
    public List<MarketSiteDomain> getMarketSiteDomainList() {
        return this.list();
    }

    @Override
    public IPage<MarketSiteDomain> getMarketSiteDomainPage(Page<MarketSiteDomain> page, MarketSiteDomain marketSiteDomain) {
        LambdaQueryWrapper<MarketSiteDomain> queryWrapper = new LambdaQueryWrapper<>();
        if (marketSiteDomain.getMarketSiteId() != null) {
            queryWrapper.eq(MarketSiteDomain::getMarketSiteId, marketSiteDomain.getMarketSiteId());
        }
        if (marketSiteDomain.getSiteDomain() != null && !marketSiteDomain.getSiteDomain().isEmpty()) {
            queryWrapper.like(MarketSiteDomain::getSiteDomain, marketSiteDomain.getSiteDomain());
        }
        if (marketSiteDomain.getStatus() != null) {
            queryWrapper.eq(MarketSiteDomain::getStatus, marketSiteDomain.getStatus());
        }
        queryWrapper.orderByDesc(MarketSiteDomain::getId);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<MarketSiteDomain> getDomainsBySiteId(Integer siteId) {
        LambdaQueryWrapper<MarketSiteDomain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketSiteDomain::getMarketSiteId, siteId);
        return this.list(queryWrapper);
    }

    @Override
    public List<MarketSiteDomain> getDomainsByStatus(Integer status) {
        LambdaQueryWrapper<MarketSiteDomain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketSiteDomain::getStatus, status);
        return this.list(queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.MARKET_SITE_DOMAIN,key = "#domain")
    public MarketSiteDomain getMarketSiteByDomain(String domain) {
        QueryWrapper<MarketSiteDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MarketSiteDomain::getSiteDomain,domain);
        return this.getOne(queryWrapper);
    }
}
