package com.console.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("r_role_menu")
public class RoleMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long roleId;//角色id
    private Long menuId;//菜单id


}
