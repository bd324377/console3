package com.console.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.admin.entity.Menu;
import com.console.admin.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色菜单关系表 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2023-09-12
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    List<Menu> selectMenusByRoleId(@Param("roleId") Integer roleId);
}
