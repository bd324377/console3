package com.console.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.core.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户Mapper接口
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
