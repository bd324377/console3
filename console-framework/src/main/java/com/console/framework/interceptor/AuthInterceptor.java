package com.console.framework.interceptor;

import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.request.PermissionRoot;
import com.console.framework.request.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull jakarta.servlet.http.HttpServletResponse response,
                             @NotNull Object handler) {
        DynamicTableNameHandler.removeTenantId();
        // 如果不是映射到方法直接通过(静态资源)
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        Method method = handlerMethod.getMethod();
        // 获取注解内容，判断是否有权限访问接口
        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        if (preAuthorize != null && preAuthorize.ignore()) {
            //无需验证权限接口直接通过
            return true;
        }
        PermissionRoot.isHasPermission(preAuthorize);
        return true;
    }
}
