package com.console.framework.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestBackendUser extends RequestUser {
    private String userName;                //用户名称
    private Integer belongTenantId;         //用户归属的租户id
    private Integer role;                   //角色
    private List<String> permissionList;    //权限列表
}
