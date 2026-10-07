package com.console.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName(value = "s_menu")
public class Menu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;//菜单ID
    private Long parentId;//上级菜单ID，0表示顶级菜单
    private String menuName;//菜单名称
    private Integer sort;//菜单排序
    private String path;//菜单路由路径
    private String componentPath;//菜单组件路径
    private MenuType type;//菜单类型：0-目录，1-菜单，2-按钮，3-展示模块
    private String permission;//权限标识
    private String icon;//菜单图标
    private Integer keepAlive;//是否缓存页面：0-不缓存，1-缓存
    @TableField(exist = false)
    private Integer isSelected;//是否选中的,用于计算菜单列表时使用


    @Getter
    public enum MenuType {
        //0、目录；1、菜单；2、页面元素
        DIRECTORY(0,"目录"),
        MENU(1,"菜单"),
        ELEMENT(2,"页面元素");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        MenuType(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
