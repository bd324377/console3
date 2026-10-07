package com.console.admin.dto.req;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.admin.entity.Menu;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.WrapperUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

public class MenuReq {
    @Getter
    @Setter
    public static class CreateMenuReq {
        private Long parentId;//上级菜单ID，0表示顶级菜单
        private String menuName;//菜单名称
        private Integer sort;//菜单排序
        private String path;//菜单路由路径
        private String componentPath;//菜单组件路径
        private Menu.MenuType type;//菜单类型：0-目录，1-菜单，2-按钮，3-展示模块
        private String permission;//权限标识
        private String icon;//菜单图标
        private Integer keepAlive;//是否缓存页面：0-不缓存，1-缓存
        private Integer status;//状态：0-停用，1-启用
        private String remark;//备注
        private List<CreateMenuReq> elements;//页面元素
    }

    @Getter
    @Setter
    public static class UpdateMenuReq {
        @NotNull
        private Integer menuId;//菜单ID
        private String menuName;//菜单名称
        private Integer sort;//菜单排序
        private String path;//菜单路由路径
        private String componentPath;//菜单组件路径
        private String permission;//权限标识
        private String icon;//菜单图标
        private Integer keepAlive;//是否缓存页面：0-不缓存，1-缓存
        private Integer status;//状态：0-停用，1-启用
        private String remark;//备注

        private UpdateWrapper<Menu> updateWrapper;
        private AdminProc adminProc;
        private String errorDesc;//错误描述

        @Getter
        public enum UpdateField {
            MENU_NAME("menuName",Menu::getMenuName),
            SORT("sort",Menu::getMenuName),
            PATH("path",Menu::getMenuName),
            COMPONENT_PATH("componentPath",Menu::getMenuName),
            PERMISSION("permission",Menu::getMenuName),
            ICON("icon",Menu::getMenuName),
            KEEP_ALIVE("keepAlive",Menu::getMenuName),
            STATUS("status",Menu::getMenuName),
            REMARK("remark",Menu::getMenuName);

            private final String fieldName;
            private final SFunction<Menu, Object> function;

            UpdateField(String fieldName,SFunction<Menu, Object> function) {
                this.fieldName = fieldName;
                this.function = function;
            }

            public static UpdateField getUpdateFieldByField(String fieldName) {
                for (UpdateField updateField : UpdateField.values()) {
                    if (updateField.fieldName.equals(fieldName)) {
                        return updateField;
                    }
                }
                throw new IllegalArgumentException("不支持的更新字段: " + fieldName);
            }
        }

        /**
         * 创建更新租户信息的参数和修改过程
         */
        public void buildUpdateParam(String part, Menu menu, RequestBackendUser backendUser) {
            if (menu == null) {
                errorDesc = "待修改菜单不存在，请确认后重试";
                return;
            }
            UpdateField updateField = UpdateField.getUpdateFieldByField(part);
            updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(Menu::getId, menu.getId());
            // 统一设置字段
            Object newValue = FieldUtils.getFieldValue(this, updateField.getFieldName());
            SFunction<Menu, Object> function = updateField.getFunction();
            Object oldValue = function.apply(menu);
            if (Objects.equals(newValue, oldValue)) {
                errorDesc = "修改值和原值一样，无需修改，请确认后充值";
            }
            WrapperUtils.setPropertyValue(updateWrapper, function, newValue);
            adminProc = new AdminProc(backendUser, AdminProc.ProcBelong.MENU, AdminProc.ProcType.UPDATE, menu.getId(), oldValue,newValue);
        }
    }
}
