package com.console.admin.dto.vo;

import com.console.admin.entity.Menu;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class MenuNode implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;//菜单ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;//上级菜单ID，0表示顶级菜单
    private String menuName;//菜单名称
    private Integer sort;//菜单排序
    private String path;//菜单路由路径
    private String componentPath;//菜单组件路径
    private Menu.MenuType type;//菜单类型：0-目录，1-菜单，2-按钮，3-展示模块
    private String permission;//权限标识
    private String icon;//菜单图标
    private Integer keepAlive;//是否缓存页面：0-不缓存，1-缓存
    private Integer isSelected;//是否选中的,用于计算菜单列表时使用
    private List<MenuNode> childMenuList;

    public MenuNode() {}
    public MenuNode(Menu menu) {
        BeanUtils.copyProperties(menu,this);
        childMenuList = new ArrayList<>();
    }

    public static List<MenuNode> buildMenuTree(List<Menu> menuList) {
        if (ObjectUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }

        // 1. 将菜单按 id 映射为 Map，便于快速查找
        Map<Long, MenuNode> menuMap = new TreeMap<>();;
        for (Menu menu : menuList) {
            menuMap.put(menu.getId(),new MenuNode(menu));
        }
        // 2. 存放最终树形列表
        List<MenuNode> treeList = new ArrayList<>();

        // 3. 遍历所有菜单，建立父子关系
        for (Menu menu : menuList) {
            Long parentId = menu.getParentId();
            // 如果 parentId 为 null 或 0，视为顶层节点
            if (parentId == null || parentId == 0) {
                treeList.add(menuMap.get(menu.getId()));
            } else {
                // 从 map 中查找父节点
                MenuNode parent = menuMap.get(parentId);
                if (parent != null) {
                    // 父节点存在，将当前菜单添加到父节点的 children 中
                    if (parent.getChildMenuList() == null) {
                        parent.setChildMenuList(new ArrayList<>());
                    }
                    parent.getChildMenuList().add(menuMap.get(menu.getId()));
                } else {
                    // 若父节点不存在（数据异常），可做容错，这里将其视为顶层节点
                    treeList.add(menuMap.get(menu.getId()));
                }
            }
        }

        // 4. 对每个节点的 children 按 sort 字段排序（可选）
        sortMenuTree(treeList);
        return treeList;
    }

    /**
     * 递归对树形菜单进行排序（按 sort 字段升序）
     * @param menuList 树形菜单列表
     */
    private static void sortMenuTree(List<MenuNode> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            return;
        }
        // 对当前层级排序
        menuList.sort((m1, m2) -> {
            int sort1 = m1.getSort() != null ? m1.getSort() : 0;
            int sort2 = m2.getSort() != null ? m2.getSort() : 0;
            return Integer.compare(sort1, sort2);
        });
        // 递归排序子节点
        for (MenuNode menu : menuList) {
            if (!ObjectUtils.isEmpty(menu.getChildMenuList())) {
                sortMenuTree(menu.getChildMenuList());
            }
        }
    }
}
