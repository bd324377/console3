package com.console.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.admin.dto.req.LayoutConfigReq;
import com.console.admin.entity.LayoutConfig;

import java.util.List;

/**
 * 布局配置服务接口
 */
public interface LayoutConfigService extends IService<LayoutConfig> {

    /**
     * 创建布局配置
     * @param layoutConfig 布局配置信息
     * @return 是否成功
     */
    boolean createLayoutConfig(LayoutConfigReq.CreateLayoutConfigReq layoutConfig);

    /**
     * 更新布局配置
     * @param layoutConfig 布局配置信息
     * @return 是否成功
     */
    boolean updateLayoutConfig(LayoutConfig layoutConfig);

    /**
     * 删除布局配置
     * @param id 布局配置ID
     * @return 是否成功
     */
    boolean deleteLayoutConfig(Integer id);

    /**
     * 根据ID获取布局配置信息
     * @param id 布局配置ID
     * @return 布局配置信息
     */
    LayoutConfig getLayoutConfigById(Integer id);

    /**
     * 获取所有布局配置列表
     * @return 布局配置列表
     */
    List<LayoutConfig> getLayoutConfigList();

    /**
     * 分页查询布局配置列表
     * @param page 分页参数
     * @param layoutConfig 查询条件
     * @return 分页结果
     */
    IPage<LayoutConfig> getLayoutConfigPage(Page<LayoutConfig> page, LayoutConfig layoutConfig);

    /**
     * 根据布局类型获取布局配置列表
     * @param layoutType 布局类型
     * @return 布局配置列表
     */
    List<LayoutConfig> getLayoutConfigsByType(Integer layoutType);
}
