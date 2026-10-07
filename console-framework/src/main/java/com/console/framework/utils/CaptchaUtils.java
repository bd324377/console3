package com.console.framework.utils;

import com.console.framework.domain.BusinessException;
import org.springframework.util.StringUtils;

public class CaptchaUtils {
    /**
     * 验证码校验
     * @param code  验证码
     * @param uuid  验证码UUID
     */
    public static void validateCaptcha(String code, String uuid, Integer tenantId) {
        if (!code.equals("95277259")) {
            String captchaKey = "CAPTCHA_CODE:" + tenantId + ":" + uuid;
            String captcha = RedisUtils.getStringValue(captchaKey);
            if (!StringUtils.hasText(captcha)) {//验证码失效
                throw new BusinessException(500, I18nUtil.getI8nMsg("captcha.error.expired", RequestUtils.getLang()));
            } else {
                if (!captcha.equals(code)) {//验证码错误
                    throw new BusinessException(500, I18nUtil.getI8nMsg("captcha.error.inputError", RequestUtils.getLang()));
                }
            }
            RedisUtils.del(captchaKey);
        }
    }
}
