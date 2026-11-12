package com.console.api.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class CaptchaReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(groups = PhoneCaptcha.class)
    private String phone;//接受短信验证码的手机号码，若遇见设置资金密码或取回资金密码，可以留空，留空发送账号绑定的手机号码
    @NotBlank(groups = MailCaptcha.class)
    private String email;//接受邮件验证码的邮箱地址，若遇见设置资金密码或取回资金密码，可以留空，留空发送账号绑定的邮箱地址
    @NotNull(groups = {PhoneCaptcha.class, MailCaptcha.class})
    private Integer type;//验证码类型，0、绑定手机号；1、绑定邮箱号；2、提现验证；3、修改密码

    public interface PhoneCaptcha {}
    public interface MailCaptcha {}
}
