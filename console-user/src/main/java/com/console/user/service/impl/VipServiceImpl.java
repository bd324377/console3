package com.console.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.user.entity.Vip;
import com.console.user.mapper.VipMapper;
import com.console.user.service.VipService;
import com.github.xiaolyuh.annotation.Cacheable;
import com.github.xiaolyuh.annotation.FirstCache;
import com.github.xiaolyuh.annotation.SecondaryCache;
import org.springframework.stereotype.Service;

@Service
public class VipServiceImpl extends BaseServiceImpl<VipMapper, Vip> implements VipService {
    @Override
    @Cacheable(
            value = CacheConstants.INITIAL_VIP,key = "T(com.console.framework.utils.TenantUtils).getTenantId()",
            firstCache = @FirstCache(expireTime = 5),secondaryCache = @SecondaryCache(expireTime = 1))
    public Integer getInitialVipId() {
        QueryWrapper<Vip> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").lambda().orderByAsc(Vip::getLevel).last("LIMIT 1");
        Vip vip = this.getOne(queryWrapper);
        return vip == null ? null : vip.getId();
    }
}
