package com.console.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.user.entity.Vip;
import com.console.user.mapper.VipMapper;
import com.console.user.model.bo.VipBo;
import com.console.user.service.VipService;
import com.github.xiaolyuh.annotation.Cacheable;
import com.github.xiaolyuh.annotation.FirstCache;
import com.github.xiaolyuh.annotation.SecondaryCache;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VipServiceImpl extends BaseServiceImpl<VipMapper, Vip> implements VipService {
    @Override
    @Cacheable(value = CacheConstants.INITIAL_VIP,key = "T(com.console.framework.utils.TenantUtils).getTenantId()")
    public Integer getInitialVipId() {
        QueryWrapper<Vip> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").lambda().orderByAsc(Vip::getLevel).last("LIMIT 1");
        Vip vip = this.getOne(queryWrapper);
        return vip == null ? null : vip.getId();
    }

    @Override
    @Cacheable(value = CacheConstants.VIP,key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #vipId")
    public VipBo getVipById(Integer vipId) {
        Vip vip = this.getById(vipId);
        if (vip == null) return null;
        VipBo vipBo = new VipBo();
        vipBo.setId(vipId);
        vipBo.setIcon(vip.getIcon());
        vipBo.setLevel(vip.getLevel());
        vipBo.setVipName(vip.getVipName());
        vipBo.setCashOutStatus(vip.getCashOutStatus());
        vipBo.setCashOutRate(vip.getCashOutRate());
        vipBo.setDayCashOutLimit(vip.getDayCashOutLimit());
        vipBo.setRechargeRate(vip.getRechargeRate());
        vipBo.setCustomerCares(vip.getCustomerCares());

        QueryWrapper<Vip> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().gt(Vip::getLevel,vip.getLevel()).orderByAsc(Vip::getLevel).last("LIMIT 1");
        Vip nextLevel = this.getOne(queryWrapper);
        if (nextLevel != null) {
            vipBo.setNextVipId(nextLevel.getId());
            vipBo.setUpgradeRechargeAmt(nextLevel.getUpgradeAmt());
            vipBo.setUpgradeOrderAmt(nextLevel.getUpgradeOrderAmt());
        }
        return vipBo;
    }

    @Override
    public List<Vip> getVipList() {
        return this.list();
    }
}
