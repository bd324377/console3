package com.console.framework.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RequestUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;                     //用户id
    private Integer tenantId;               //用户登录的租户id
    private String  account;                //账号
    private String  avatar;                 //头像
    private Integer userType;               //1、用户 2、管理后台用户 3、渠道后台用户 4、渠道业务员用户
    private String  os;                     //操作系统
    private String  browser;                //浏览器
    private String  ip;                     //登录ip
}
