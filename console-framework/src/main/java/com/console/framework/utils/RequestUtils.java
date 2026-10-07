package com.console.framework.utils;

import com.console.framework.request.RequestBackendUser;
import com.console.framework.request.RequestMember;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
public class RequestUtils {
    /**
     * 获取单前请求
     **/
    public HttpServletRequest getCurrentRequest() throws IllegalStateException {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    public String getRequestIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return IpUtils.getClientIp(request);
    }

    /**
     * 获取当前请求的完整路径
     * @return 请求url，包括：域名，端口，上下文访问路径
     */
    public String getCurrentRequestUrl() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getServletPath();
    }

    /**
     * 获取当前请求的源域名
     */
    public String getRequestDomain() {
        try {
            String origin = getCurrentRequest().getHeader("Referer");
            if (origin == null) {
                return null;
            }
            origin = origin.replaceFirst("^(https?://)?", "");
            origin = origin.replaceFirst("^(http?://)?", "");
            return origin.split("/")[0];
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前访问token
     */
    public String getTokenKey() {
        HttpServletRequest httpServletRequest = getCurrentRequest();
        if (httpServletRequest == null) {
            return null;
        }
        String token = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            token = token.replace("Bearer", "");
            token = token.replace(" ", "");
            return TokenUtils.getRequestToken(token);
        }
        return null;
    }

    public Integer getRequestTenantId() {
        String tokenKey = getTokenKey();
        if (StringUtils.hasText(tokenKey)) {
            String[] tokenInfos = tokenKey.split(":");
            if (tokenInfos.length > 2) {
                String tokenIdStr = tokenInfos[1];
                return Integer.valueOf(tokenIdStr);
            }
        }
        return null;
    }

    public RequestMember getRequestMember() {
        return getUser(RequestMember.class);
    }

    public RequestBackendUser getRequestBackendUser() {
        return getUser(RequestBackendUser.class);
    }

    /**
     * 获取当前访问用户
     */
    public <T> T getUser(Class<T> beanClass) {
        try {
            String tokenKey = RequestUtils.getTokenKey();
            if (StringUtils.hasText(tokenKey)) {
                return TokenUtils.getRequestUser(tokenKey,beanClass);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前请求需要返回的语种
     */
    public String getLang() {
        HttpServletRequest httpServletRequest = getCurrentRequest();
        if (httpServletRequest == null) {
            return "pt";
        }
        return httpServletRequest.getHeader("Accept-Language") == null ? "en" : httpServletRequest.getHeader("Accept-Language");
    }
}
