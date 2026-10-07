package com.console.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.console.admin.dto.req.MenuReq;
import com.console.admin.dto.vo.MenuNode;
import com.console.core.entity.AdminProc;
import com.console.admin.entity.Menu;
import com.console.core.mapper.AdminProcMapper;
import com.console.admin.mapper.MenuMapper;
import com.console.admin.service.MenuService;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl extends BaseServiceImpl<MenuMapper,Menu> implements MenuService {
    @Resource
    private AdminProcMapper adminProcMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMenu(MenuReq.CreateMenuReq createMenuReq) {
        RequestBackendUser backendUser = RequestUtils.getUser(RequestBackendUser.class);
        List<Menu> menuList = new ArrayList<>();
        Menu menu = new Menu();
        BeanUtils.copyProperties(createMenuReq,menu);
        menu.setId(IdWorker.getId());
        menuList.add(menu);
        Menu parentMenu = this.getById(createMenuReq.getParentId());
        if (createMenuReq.getParentId() != 0 && parentMenu == null) {
            throw new BusinessException(500,"添加菜单失败，父菜单不存在，请确认后重试");
        }
        if (createMenuReq.getType() == Menu.MenuType.ELEMENT) {
            if (parentMenu == null || parentMenu.getType() != Menu.MenuType.MENU) {//非菜单页面不可添加页面元素
                throw new BusinessException(500,"添加菜单失败，非菜单页面不可添加页面元素");
            }
        } else {
            if (createMenuReq.getParentId() != 0 && parentMenu.getType() != Menu.MenuType.DIRECTORY) {
                throw new BusinessException(500,"添加菜单失败，添加目录或菜单必须选择上级目录");
            }
            if (createMenuReq.getType() == Menu.MenuType.MENU && !ObjectUtils.isEmpty(createMenuReq.getElements())) {
                for (MenuReq.CreateMenuReq elementItem : createMenuReq.getElements()) {
                    Menu element = new Menu();
                    BeanUtils.copyProperties(elementItem,element);
                    element.setId(IdWorker.getId());
                    element.setParentId(menu.getId());
                    menuList.add(element);
                }
            }
        }
        if (this.saveBatch(menuList)) {
            AdminProc adminProc = new AdminProc(backendUser, AdminProc.ProcBelong.MENU, AdminProc.ProcType.ADD,menu.getId(),null,menuList);
            adminProcMapper.insert(adminProc);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delMenuById(Long menuId) {
        Menu menu = this.getById(menuId);
        if (menu != null && this.removeById(menuId)) {
            if (menu.getType() != Menu.MenuType.ELEMENT) {//删除目录和菜单需要同步删除其下级菜单和元素
                QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(Menu::getParentId,menuId);
                this.remove(queryWrapper);
            }
            return true;
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(String part, MenuReq.UpdateMenuReq updateMenuReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getUser(RequestBackendUser.class);
        Menu menu = this.getById(updateMenuReq.getMenuId());
        updateMenuReq.buildUpdateParam(part,menu,requestBackendUser);
        if (StringUtils.hasText(updateMenuReq.getErrorDesc())) {
            throw new BusinessException(500,updateMenuReq.getErrorDesc());
        }
        if (updateMenuReq.getUpdateWrapper() != null && this.update(updateMenuReq.getUpdateWrapper())) {
            adminProcMapper.insert(updateMenuReq.getAdminProc());//添加修改日志
            return true;
        }
        return false;
    }

    @Override
    public List<MenuNode> getMenuTree() {
        List<Menu> menuList = this.list();
        return MenuNode.buildMenuTree(menuList);
    }


}
