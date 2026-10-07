package com.console.api.dto.req;

import com.console.framework.constants.LoginChannel;
import com.console.framework.utils.EncryptUtils;
import com.console.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class RegReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer accountType = 3;   //账号类型，1为邮箱地址登录，2为手机号码登录，3为账号登录
    @NotBlank(message = "reg.error.account.blank")
    private String  account;           //账号
    @NotBlank(message = "reg.error.password.blank")
    private String  password;          //密码
    private String  regDeviceId;       //设备id
    private String  inviteCode;        //邀请码
    private String  phone;             //手机号
    private LoginChannel regChannel;   //注册渠道

    public User initUser() {
        User user = new User();
        user.setAccount(this.getAccount());
        user.setLoginPwd(EncryptUtils.md5Encrypt(this.getPassword()));
        user.setRegDeviceId(this.getRegDeviceId());
        user.setLoginChannel(this.getRegChannel());
        if (this.getAccountType() == 1) {//邮箱注册
            user.setEmail(this.getAccount());
        } else if (this.getAccountType() == 2) {//手机号注册
            user.setPhone(this.getAccount());
        }
        if (StringUtils.hasText(this.getPhone())) {
            user.setPhone(this.getPhone());
        }
        return user;
    }
}
