package com.console.admin.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleMenuReq {
    @NotNull
    private Long roleId;            //角色id
    private List<Long> menuIdList;  //菜单id
}
