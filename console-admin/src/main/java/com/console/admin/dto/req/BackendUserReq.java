package com.console.admin.dto.req;

import com.console.admin.entity.BackendUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BackendUserReq {
    @Getter
    @Setter
    public static class CreateBackendUserReq {
        @NotBlank
        private String  account;            //登录账号
        @NotNull
        private BackendUser.AccountType accountType;    //账号类型：0-后台管理用户，1-分销用户
        @NotBlank
        private String  userName;           //管理员名称
        @NotNull
        private String  loginPwd;           //登录密码
        private String  loginIp;            //最后登录IP
        private BackendUser.BackendUserStatus status;   //用户状态：0-停用，1-启用
        private List<String> accessibleIps;      //可访问IP列表（JSON数组格式）
        private List<Integer> accessibleTenants;  //可访问租户列表（JSON数组格式）
    }

    @Getter
    @Setter
    public static class LoginReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotBlank(message = "backendUser.error.login.account.blank")
        private String account;            //登录账号
        @NotBlank(message = "backendUser.error.login.loginPwd.blank")
        private String loginPwd;           //登录密码
        @NotBlank(message = "backendUser.error.login.captcha.blank")
        private String captcha;        //登录验证码
        @NotBlank(message = "backendUser.error.login.uuid.blank")
        private String uuid;        //验证码对应唯一标识
        @NotNull(message = "backendUser.error.login.tenantId.blank")
        private Integer tenantId;    //租户标识
    }

    @Getter
    @Setter
    public static class RefreshTokenReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotBlank
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class SearchBackendUserReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private long size = 50;
        private long current = 1;
        private String account;
        private Integer roleId;
    }
}
