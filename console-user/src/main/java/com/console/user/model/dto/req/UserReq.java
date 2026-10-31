package com.console.user.model.dto.req;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.WrapperUtils;
import com.console.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

public class UserReq {
    @Getter
    @Setter
    public static class CreateUserReq implements Serializable {//测试用户不设置站点相关信息，整个租户全站点通用
        @Serial
        private static final long serialVersionUID = 1L;

        private String  accountPrefix;//登录账号前缀
        private Integer accountType;//账号类型: 1、(纯字母),2、(纯数字),  3、(字母数字混合)
        private String  loginPwd;//登录密码
        @NotNull
        private BigDecimal userAmt;             //用户钱包余额
        @NotNull
        private Integer accountCount;       //生成账号数量
        @NotNull
        private Integer accountLength;      //账号长度

        private static final String NUMBERS = "0123456789";
        private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        /**
         * 生成随机账号
         * @return 随机生成的账号字符串
         */
        public String generateAccount() {
            if (accountLength == null || accountLength <= 0) {
                accountLength = 6;
            }

            Random random = new Random();
            StringBuilder account = new StringBuilder();
            String charSet = switch (accountType) {//账号类型: 1、(纯字母),2、(纯数字),  3、(字母数字混合)
                case 1 -> LETTERS;
                case 2 -> NUMBERS;
                case 3 -> ALPHANUMERIC;
                default -> throw new IllegalArgumentException("不支持的账号类型: " + accountPrefix +
                        "。请使用: 1、(纯字母),2、(纯数字),  3、(字母数字混合)");
            };

            for (int i = 0; i < accountLength; i++) {
                int index = random.nextInt(charSet.length());
                account.append(charSet.charAt(index));
            }
            if (StringUtils.hasText(accountPrefix)) {
                account.insert(0, accountPrefix);
            }
            return account.toString();
        }
    }

    @Getter
    @Setter
    public static class UpdateUserReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        @NotNull
        private Integer id;//用户id
        private String loginPwd;//登录密码
        private String avatar;  //头像
        private Integer inviteRewardStatus;//邀请奖励状态（是否能参与邀新活动：1、能；0不能）
        private String phone;//电话号码
        private String email;//邮箱账号
        private Integer status;//账号状态：1、正常；2、冻结；
        private Integer cashOutStatus;  //个人提现状态
        private Integer vipId;//VIP等级id
        private Integer agentId;//代理等级id
        private String cashOutPwd;//提现密码
        private String remarks;//备注

        private UpdateWrapper<User> updateWrapper;
        private AdminProc adminProc;
        private String errorDesc;

        @Getter
        public enum UpdateField {
            LOGIN_PWD("loginPwd",User::getLoginPwd,"user.proc.update.loginPwd"),
            AVATAR("avatar",User::getLoginPwd,"user.proc.update.avatar"),
            NICK_NAME("nickName",User::getLoginPwd,"user.proc.update.nickName"),
            INVITE_REWARD_STATUS("inviteRewardStatus",User::getLoginPwd,"user.proc.update.inviteRewardStatus"),
            PHONE("phone",User::getLoginPwd,"user.proc.update.phone"),
            EMAIL("email",User::getLoginPwd,"user.proc.update.email"),
            STATUS("status",User::getLoginPwd,"user.proc.update.status"),
            CASH_OUT_STATUS("cashOutStatus",User::getLoginPwd,"user.proc.update.cashOutStatus"),
            VIP_ID("vipId",User::getLoginPwd,"user.proc.update.vipId"),
            AGENT_ID("agentId",User::getLoginPwd,"user.proc.update.agentId"),
            CASH_OUT_PWD("cashOutPwd",User::getLoginPwd,"user.proc.update.cashOutPwd"),
            REMARKS("remarks",User::getLoginPwd,"user.proc.update.remarks");
            private final String fieldName;
            private SFunction<User, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<User, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }

            public static UpdateField getUpdateFieldByField(String fieldName) {
                for (UpdateField updateField : UpdateField.values()) {
                    if (updateField.fieldName.equals(fieldName)) {
                        return updateField;
                    }
                }
                throw new IllegalArgumentException("不支持的更新字段: " + fieldName);
            }
        }

        public void buildUpdateParam(String part, User user, RequestBackendUser backendUser) {
            if (user == null) {
                errorDesc = "待修改用户不存在，请确认后重试";
                return;
            }
            UpdateField updateField = UpdateField.getUpdateFieldByField(part);
            updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(User::getId, user.getId());
            // 统一设置字段
            Object newValue = FieldUtils.getFieldValue(this, updateField.getFieldName());
            SFunction<User, Object> function = updateField.getFunction();
            Object oldValue = function.apply(user);
            if (Objects.equals(newValue, oldValue)) {
                errorDesc = "修改值和原值一样，无需修改，请确认后重试";
            }
            WrapperUtils.setPropertyValue(updateWrapper, function, newValue);
            adminProc = new AdminProc(backendUser, AdminProc.ProcBelong.USER,
                    AdminProc.ProcType.UPDATE, user.getId(), oldValue,newValue);
        }
    }

    @Getter
    @Setter
    public static class SearchUserReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
