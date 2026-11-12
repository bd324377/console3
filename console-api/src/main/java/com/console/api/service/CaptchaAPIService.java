package com.console.api.service;

import com.console.api.dto.req.CaptchaReq;

public interface CaptchaAPIService {
    String sendSmsCaptcha(CaptchaReq captchaReq);
}
