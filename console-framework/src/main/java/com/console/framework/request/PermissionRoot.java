package com.console.framework.request;

import com.console.framework.domain.BusinessException;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RedisUtils;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class PermissionRoot {

    public static void debounceHandle() {
//        RequestUser user = RequestUtils.getUser();
//        String debounceKey = "DEBOUNCE:INTERFACE_" + RequestUtils.getCurrentRequestUrl() + ":";
//        if (user == null) {//判断IP，根据IP做防抖处理
//            debounceKey = debounceKey + RequestUtils.getCurrentIp();
//        } else {//根据用户ID做防抖处理
//            debounceKey = debounceKey + user.getId();
//        }
//        long times = RedisUtils.increment(debounceKey,1,1);
//        if (times > 3) {
//            throw new CustomException(429, I18nUtil.getI8nMsg("http.error.busy"));
//        }
    }
    public static void isHasPermission(PreAuthorize preAuthorize) {
        String tokenKey = RequestUtils.getTokenKey();
        if (StringUtils.hasText(tokenKey)) {
            Long expire = RedisUtils.getExpire(tokenKey);
            if (expire == -2) {//token不存在
                throw new BusinessException(401, I18nUtil.getI8nMsg("http.error.401"));
            }
            if (expire < 180) {//attoken有效时间小于三分钟则刷新at token过期时间
                TokenUtils.refreshAccessTokenExpire(tokenKey);
            }
        } else {
            throw new BusinessException(401, I18nUtil.getI8nMsg("http.error.401"));
        }
        RequestUser user = RequestUtils.getUser(RequestUser.class);
        if (user == null || user.getTenantId() == null) throw new BusinessException(401, "登录状态无效");
        if (preAuthorize != null) {
            boolean member = Integer.valueOf(1).equals(user.getUserType());
            boolean backendAccount = List.of(2, 3, 4).contains(user.getUserType() == null ? 0 : user.getUserType());
            if ((preAuthorize.accountType() == 1 && !backendAccount) || (preAuthorize.accountType() == 2 && !member))
                throw new BusinessException(403, "账号类型无权限");
            if (preAuthorize.loginAccount().length > 0 && !Arrays.asList(preAuthorize.loginAccount()).contains(user.getAccount()))
                throw new BusinessException(403, "账号无权限");
            if (!preAuthorize.value().isBlank()) {
                RequestBackendUser backend = RequestUtils.getRequestBackendUser();
                if (member || backend == null || backend.getPermissionList() == null || !backend.getPermissionList().contains(preAuthorize.value()))
                    throw new BusinessException(403, "没有操作权限");
            }
        }
        DynamicTableNameHandler.setTenantId(user.getTenantId());
    }
}
