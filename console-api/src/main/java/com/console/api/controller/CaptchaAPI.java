package com.console.api.controller;

import com.console.api.dto.req.CaptchaReq;
import com.console.api.service.CaptchaAPIService;
import com.console.framework.domain.Result;
import com.console.framework.request.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaAPI {
    @Resource
    private CaptchaAPIService captchaAPIService;

    /**
     * 发送短信验证码
     * @param captchaReq    获取验证码参数
     */
    @PostMapping("/smsCaptcha")
    @PreAuthorize(ignore = true)
    public Result sendSmsCaptcha(@RequestBody @Validated(CaptchaReq.PhoneCaptcha.class) CaptchaReq captchaReq) {
        return Result.success(captchaAPIService.sendSmsCaptcha(captchaReq));
    }
}
