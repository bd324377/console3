package com.console.api.service.impl;

import com.console.api.dto.req.CaptchaReq;
import com.console.api.service.CaptchaAPIService;
import org.springframework.stereotype.Service;

@Service
public class CaptchaAPIServiceImpl extends BaseServiceImpl implements CaptchaAPIService {

    @Override
    public String sendSmsCaptcha(CaptchaReq captchaReq) {
        return null;
    }
}
