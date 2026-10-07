package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.admin.dto.req.LayoutConfigReq;
import com.console.admin.entity.LayoutConfig;
import com.console.admin.mapper.LayoutConfigMapper;
import com.console.admin.service.LayoutConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 布局配置服务实现类
 */
@Slf4j
@Service
public class LayoutConfigServiceImpl extends ServiceImpl<LayoutConfigMapper, LayoutConfig> implements LayoutConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createLayoutConfig(LayoutConfigReq.CreateLayoutConfigReq createLayoutConfigReq) {
        LayoutConfig layoutConfig = new LayoutConfig();
        BeanUtils.copyProperties(createLayoutConfigReq,layoutConfig);
        return this.updateById(layoutConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLayoutConfig(LayoutConfig layoutConfig) {
        return this.updateById(layoutConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLayoutConfig(Integer id) {
        return this.removeById(id);
    }

    @Override
    public LayoutConfig getLayoutConfigById(Integer id) {
        return this.getById(id);
    }

    @Override
    public List<LayoutConfig> getLayoutConfigList() {
        return this.list();
    }

    @Override
    public IPage<LayoutConfig> getLayoutConfigPage(Page<LayoutConfig> page, LayoutConfig layoutConfig) {
        LambdaQueryWrapper<LayoutConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (layoutConfig.getLayoutType() != null) {
            queryWrapper.eq(LayoutConfig::getLayoutType, layoutConfig.getLayoutType());
        }
        if (layoutConfig.getShowName() != null && !layoutConfig.getShowName().isEmpty()) {
            queryWrapper.like(LayoutConfig::getShowName, layoutConfig.getShowName());
        }
        if (layoutConfig.getDevModeEnabled() != null) {
            queryWrapper.eq(LayoutConfig::getDevModeEnabled, layoutConfig.getDevModeEnabled());
        }
        queryWrapper.orderByDesc(LayoutConfig::getId);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<LayoutConfig> getLayoutConfigsByType(Integer layoutType) {
        LambdaQueryWrapper<LayoutConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LayoutConfig::getLayoutType, layoutType);
        return this.list(queryWrapper);
    }
}
