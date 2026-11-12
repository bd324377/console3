package com.console.api.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;

public class UserReq {
    @Getter
    @Setter
    public static class UpdatePwdReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotBlank
        private String password;//旧密码
        @NotBlank
        private String newPassword;//新密码，8-16位长度
        @NotBlank
        private String againPassword;//确认新密码，必须确保与password字段内容一致
    }

    @Getter
    @Setter
    public static class BindEmailReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private String email;       //要绑定的邮箱
        @NotNull
        private String verifyCode;  //验证码
    }

    @Getter
    @Setter
    public static class BindPhoneReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @NotNull
        private String phone;       //要绑定的手机号码
        @NotNull
        private String verifyCode;  //验证码
    }

    @Getter
    @Setter
    public static class UpdateUserInfoReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String nickName;//玩家昵称
        @Range(min = 1, max = 2, message = "Sex must be either 1 or 2")
        private Integer sex;//性别，1为女，2为男
        private Integer birthdayDay;//生日
        private String avatar;      //头像地址，上传头像后需调用该接口递交更新头像地址

        public boolean isUpdate() {
            return StringUtils.hasText(nickName) || StringUtils.hasText(avatar) || sex != null || birthdayDay != null;
        }
    }

    @Getter
    @Setter
    public static class SearchSubUserReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Integer startSearchTime;    //起始查询时间
        private Integer endSearchTime;      //结束查询时间
        private Integer userId;             //用户ID
        private Integer subUserId;          //下级用户ID查询
        private Long    size = 20L;         //每页条数
        private Long    current = 1L;       //当前页码
        private Integer layers;
        private Integer startSearchLayer;         //起始层级
        private Integer endSearchLayer;           //结束层级
    }
}
