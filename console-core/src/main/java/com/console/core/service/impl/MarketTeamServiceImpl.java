package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.MarketTeam;
import com.console.core.mapper.MarketTeamMapper;
import com.console.core.service.MarketTeamService;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.github.xiaolyuh.annotation.Cacheable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 营销团队服务实现类
 */
@Slf4j
@Service
public class MarketTeamServiceImpl extends BaseServiceImpl<MarketTeamMapper, MarketTeam> implements MarketTeamService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMarketTeam(MarketTeam marketTeam) {
        return this.save(marketTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMarketTeam(MarketTeam marketTeam) {
        return this.updateById(marketTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMarketTeam(Integer id) {
        return this.removeById(id);
    }

    @Override
    @Cacheable(value = CacheConstants.MARKET_TEAM,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #id")
    public MarketTeam getMarketTeamById(Integer id) {
        QueryWrapper<MarketTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,market_site_id,team_name,leader_marketer_id,status,order_mode,shunt_status,shunt_rate,shunt_threshold")
                .lambda().eq(MarketTeam::getId,id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Cacheable(value = CacheConstants.DEFAULT_MARKET_TEAM,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #marketSiteId")
    public MarketTeam getDefaultMarketTeam(Integer marketSiteId) {
        QueryWrapper<MarketTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,market_site_id,team_name,leader_marketer_id,status,order_mode,shunt_status,shunt_rate,shunt_threshold")
                .lambda().eq(MarketTeam::getMarketSiteId,marketSiteId).eq(MarketTeam::getIdDefaultTeam,1);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<MarketTeam> getMarketTeamList() {
        return this.list();
    }

    @Override
    public IPage<MarketTeam> getMarketTeamPage(Page<MarketTeam> page, MarketTeam marketTeam) {
        LambdaQueryWrapper<MarketTeam> queryWrapper = new LambdaQueryWrapper<>();
        if (marketTeam.getTeamName() != null && !marketTeam.getTeamName().isEmpty()) {
            queryWrapper.like(MarketTeam::getTeamName, marketTeam.getTeamName());
        }
        if (marketTeam.getLeaderMarketerId() != null) {
            queryWrapper.eq(MarketTeam::getLeaderMarketerId, marketTeam.getLeaderMarketerId());
        }
        if (marketTeam.getStatus() != null) {
            queryWrapper.eq(MarketTeam::getStatus, marketTeam.getStatus());
        }
        if (marketTeam.getLayoutId() != null) {
            queryWrapper.eq(MarketTeam::getLayoutId, marketTeam.getLayoutId());
        }
        queryWrapper.orderByDesc(MarketTeam::getCreateTime);
        return this.page(page, queryWrapper);
    }
}
