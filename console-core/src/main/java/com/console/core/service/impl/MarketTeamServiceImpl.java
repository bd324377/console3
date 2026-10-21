package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.AdminProc;
import com.console.core.entity.MarketTeam;
import com.console.core.entity.Marketer;
import com.console.core.mapper.MarketTeamMapper;
import com.console.core.mapper.MarketerMapper;
import com.console.core.merger.AdminProcMerger;
import com.console.core.model.dto.req.MarketTeamReq;
import com.console.core.service.MarketTeamService;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.RequestUtils;
import com.github.xiaolyuh.annotation.Cacheable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 营销团队服务实现类
 */
@Slf4j
@Service
public class MarketTeamServiceImpl extends BaseServiceImpl<MarketTeamMapper, MarketTeam> implements MarketTeamService {
    @Resource
    private MarketerMapper marketerMapper;
    @Resource
    private AdminProcMerger adminProcMerger;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMarketTeam(MarketTeamReq.CreateMarketTeamReq createMarketTeamReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        MarketTeam marketTeam = new MarketTeam();
        BeanUtils.copyProperties(createMarketTeamReq,marketTeam);
        try {
            if (this.save(marketTeam)) {
                Marketer marketer = new Marketer();//创建默认营业员
                BeanUtils.copyProperties(marketTeam, marketer);
                marketer.setMarketTeamId(marketTeam.getId());
                marketer.setMarketSiteId(marketTeam.getMarketSiteId());
                marketer.setMarketerId(marketTeam.getLeaderMarketerId());
                marketer.setIsLeader(1);
                marketer.setOrderMode(marketTeam.getOrderMode());
                marketerMapper.insert(marketer);

                AdminProc createTeamAdminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.MARKET_TEAM,
                        AdminProc.ProcType.ADD, marketTeam.getId(), null, marketTeam);
                adminProcMerger.submit(createTeamAdminProc);
                AdminProc createMarketerAdminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.MARKETER,
                        AdminProc.ProcType.ADD, marketer.getId(), null, marketer);
                adminProcMerger.submit(createMarketerAdminProc);
                return true;
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException(500,"运营团队创建失败，该业务员已加入该站点其它运营团队");
        }
        return false;
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
