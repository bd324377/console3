package com.console.api.dto.req;

import com.console.framework.constants.LoginChannel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReq {
    @NotBlank(message = "login.error.account.notNull")
    private String account;     //账号
    @NotBlank(message = "login.error.password.notNull")
    private String password;    //密码
    private LoginChannel loginChanel; //登录渠道
    private String endpoint;    //浏览器推送唯一地址
}
