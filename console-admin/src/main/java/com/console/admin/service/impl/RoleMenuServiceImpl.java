package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.admin.dto.req.RoleMenuReq;
import com.console.admin.dto.vo.MenuNode;
import com.console.admin.dto.vo.Role;
import com.console.admin.entity.BackendUser;
import com.console.admin.entity.Menu;
import com.console.admin.entity.RoleMenu;
import com.console.admin.mapper.RoleMenuMapper;
import com.console.admin.service.MenuService;
import com.console.admin.service.RoleMenuService;
import com.console.core.entity.AdminProc;
import com.console.core.mapper.AdminProcMapper;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.ResultCode;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
    @Resource
    private MenuService menuService;
    @Resource
    private AdminProcMapper adminProcMapper;

    @Override
    public List<Role> getRoleList() {
        return Arrays.stream(BackendUser.RoleType.values()).map(item -> {
            Role role = new Role();
            role.setRoleId(item.getValue());
            role.setRoleName(item.getDesc());
            return role;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOrUpdateRoleMenus(RoleMenuReq roleMenuReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();

        if (!Objects.equals(requestBackendUser.getRole(), BackendUser.RoleType.SUPER_ADMIN.getValue()) && !Objects.equals(requestBackendUser.getRole(), BackendUser.RoleType.SUPER_ADMIN.getValue())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED_FAILED);
        }
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RoleMenu::getRoleId, roleMenuReq.getRoleId());
        this.baseMapper.delete(queryWrapper);
        List<RoleMenu> newRoleMenuList = new ArrayList<>();
        for (Long menuId : roleMenuReq.getMenuIdList()) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleMenuReq.getRoleId());
            roleMenu.setMenuId(menuId);
            newRoleMenuList.add(roleMenu);
        }
        if (this.saveBatch(newRoleMenuList)) {
            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.ROLE_MENU, AdminProc.ProcType.UPDATE, roleMenuReq.getRoleId(), null, null);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    public List<MenuNode> getMenusByRequestUser() {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        DynamicTableNameHandler.setTenantId(requestBackendUser.getBelongTenantId());
        if (Objects.equals(requestBackendUser.getRole(), BackendUser.RoleType.SUPER_ADMIN.getValue())) {
            return menuService.getMenuTree();
        } else {
            List<Menu> menuList = this.baseMapper.selectMenusByRoleId(requestBackendUser.getRole());
            return MenuNode.buildMenuTree(menuList);
        }
    }

    @Override
    public List<MenuNode> getMenusByRoleIdForUpdate(Integer roleId) {
        List<Menu> requestUserMenuList;
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        if (Objects.equals(requestBackendUser.getRole(), BackendUser.RoleType.SUPER_ADMIN.getValue())) {
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id,parent_id,menu_name,sort,path,component_path,type,permission,icon");
            requestUserMenuList = menuService.list(queryWrapper);
        } else {
            requestUserMenuList = this.baseMapper.selectMenusByRoleId(requestBackendUser.getRole());
        }
        List<Menu> roleMenuList = this.baseMapper.selectMenusByRoleId(roleId);
        List<Long> roleMenuIdList = roleMenuList.stream().map(Menu::getId).toList();
        for (Menu menu : requestUserMenuList) {
            if (roleMenuIdList.contains(menu.getId())) {
                menu.setIsSelected(1);
            } else {
                menu.setIsSelected(0);
            }
        }
        return MenuNode.buildMenuTree(requestUserMenuList);
    }

}
