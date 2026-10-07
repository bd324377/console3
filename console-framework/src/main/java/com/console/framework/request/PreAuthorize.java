package com.console.framework.request;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuthorize {
    String value() default "";          //访问所需权限值
    String[] loginAccount() default {};   //访问所需登录账号
    int accountType() default 0;    //账号类型（0，所有都可以；1、管理员账号；2、用户账号）
    boolean ignore() default false;       //是否忽视验证
}
