package com.console.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.admin.dto.req.RoleMenuReq;
import com.console.admin.dto.vo.MenuNode;
import com.console.admin.dto.vo.Role;
import com.console.admin.entity.RoleMenu;

import java.util.List;

public interface RoleMenuService extends IService<RoleMenu> {
    List<Role> getRoleList();
    boolean addOrUpdateRoleMenus(RoleMenuReq roleMenuReq);
    List<MenuNode> getMenusByRequestUser();
    List<MenuNode> getMenusByRoleIdForUpdate(Integer roleId);
}
