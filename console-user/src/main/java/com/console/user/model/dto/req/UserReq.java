package com.console.user.model.dto.req;

import com.console.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserReq {
    @Getter
    @Setter
    public static class CreateUserReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String  account;//登录账号
        private Integer accountType;//账号类型
        private String  loginPwd;//登录密码
        private Integer parentId;//上级用户ID
        private Integer realParentId;//真实上级用户ID
        private Integer tenantId;//租户ID
        private Integer marketSiteId;//站点ID
        private Integer marketTeamId;//团队（渠道）ID
        private Integer marketerId;//业务员Id
        private String  regIp;//注册IP
        private String  regDeviceId;//注册设备ID

        public User buildUser() {
            User user = new User();
            BeanUtils.copyProperties(this,user);
            user.setRegTime(LocalDateTime.now());

            return user;
        }
    }

    @Getter
    @Setter
    public static class BathCreateUserReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String  accountPrefix;      //账号前缀
        private Integer accountType;        //账号类型  1、邮箱；2、手机号
        private Integer accountCount;       //生成账号数量
        private BigDecimal amt;             //余额
        private Integer accountLength;      //账号长度
    }
}
