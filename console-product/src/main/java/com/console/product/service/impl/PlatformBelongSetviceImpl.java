package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.BusinessException;
import com.console.product.entity.PlatformBelong;
import com.console.product.mapper.PlatformBelongMapper;
import com.console.product.service.PlatformBelongService;
import com.github.xiaolyuh.annotation.CacheEvict;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformBelongSetviceImpl extends BaseServiceImpl<PlatformBelongMapper, PlatformBelong> implements PlatformBelongService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PLATFORM_BELONG,key = "T(com.console.framework.utils.TenantUtils).getTenantId()")
    public boolean updatePlatformBelongStatus(PlatformBelong platformBelong) {
        if (platformBelong.getStatus() == 1) {//开启，需要考虑只能开启一个
            UpdateWrapper<PlatformBelong> openUpdateWrapper = new UpdateWrapper<>();
            openUpdateWrapper.lambda().eq(PlatformBelong::getId,platformBelong.getId()).set(PlatformBelong::getStatus,1);
            UpdateWrapper<PlatformBelong> closeUpdateWrapper = new UpdateWrapper<>();
            closeUpdateWrapper.lambda().ne(PlatformBelong::getId,platformBelong.getId()).set(PlatformBelong::getStatus,0);
            return this.update(openUpdateWrapper) && this.update(closeUpdateWrapper);
        } else {//关闭，必须有一个开启
            throw new BusinessException(500,"关闭失败，系统必须开启一个产商归属");
        }
    }

    @Override
    @Cacheable(value = CacheConstants.PLATFORM_BELONG,key = "#tenantId")
    public PlatformBelong getOpenPlatformBelong(Integer tenantId) {
        QueryWrapper<PlatformBelong> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PlatformBelong::getStatus,1).last("limit 1");
        return this.getOne(queryWrapper);
    }
}
