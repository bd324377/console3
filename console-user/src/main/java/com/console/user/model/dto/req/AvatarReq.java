package com.console.user.model.dto.req;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import com.console.user.entity.Avatar;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class AvatarReq {

    @Getter
    @Setter
    public static class DelAvatarReq {
        @NotNull
        private Integer avatarId;
    }

    @Getter
    @Setter
    public static class UpdateAvatarReq extends BaseUpdateReq<Avatar> {
        @NotNull
        private Integer id;
        @NotNull
        private Integer marketSiteId;

        private Integer sex;    //性别 1、男；0、女
        private String  avatarPath;//头像地址
        private String  avatarName;//头像名称

        @Getter
        public enum UpdateField implements IUpdateField<Avatar> {
            CUSTOMER_CARE_NAME("sex",Avatar::getSex,"customerCare.proc.update.sex"),
            ICON("avatarPath",Avatar::getAvatarPath,"customerCare.proc.update.avatarPath"),
            ROUTE_PATH("avatarName",Avatar::getAvatarName,"customerCare.proc.update.avatarName");

            private final String fieldName;
            private final SFunction<Avatar, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<Avatar, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }

        @Override
        protected SFunction<Avatar, ?> idFunction() {
            return Avatar::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.AVATAR;
        }

        @Override
        protected IUpdateField<Avatar> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }
}
