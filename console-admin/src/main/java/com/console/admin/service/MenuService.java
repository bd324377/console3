package com.console.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.admin.dto.req.MenuReq;
import com.console.admin.dto.vo.MenuNode;
import com.console.admin.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    boolean createMenu(MenuReq.CreateMenuReq createMenuReq);
    boolean delMenuById(Long menuId);
    boolean updateMenu(String part, MenuReq.UpdateMenuReq updateMenuReq);
    List<MenuNode> getMenuTree();
}
