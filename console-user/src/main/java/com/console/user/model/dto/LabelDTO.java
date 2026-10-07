package com.console.user.model.dto;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.entity.AdminProc;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.request.RequestUser;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.WrapperUtils;
import com.console.user.entity.Label;
import lombok.Getter;
import lombok.Setter;

public class LabelDTO {
    @Getter
    @Setter
    public static class UpdateLabelDTO extends Label {
        private UpdateWrapper<Label> updateWrapper;
        private AdminProc adminProc;

        /**
         * 创建更新标签信息的参数和过程
         * @param part      更新部分
         * @param label     待更新标签旧信息
         */
        public void buildUpdateParam(String part, Label label) {
            if (label != null) {
                RequestBackendUser admin = RequestUtils.getRequestBackendUser();
//                adminProc     = new AdminProc(admin, AdminProc.ProcBelong.LABEL_MANAGE, AdminProc.ProcType.UPDATE_LABEL, this.getId());
                updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().eq(Label::getId,this.getId()).set(Label::getUpdateTime, DateUtils.getCurrentTimestamp());
                switch (part) {
                    case "labelName" -> {//修改标签名称
                        WrapperUtils.setPropertyValue(updateWrapper, Label::getLabelName, this.getLabelName());
//                        adminProc.setObjectValue(this.getLabelName());
//                        adminProc.setObjectBeforeValue(label.getLabelName());
//                        adminProc.setDescription("label.proc.update.labelName");
                    }
                    case "labelType" -> {//修改标签类型
                        WrapperUtils.setPropertyValue(updateWrapper, Label::getLabelType, this.getLabelType());
//                        adminProc.setObjectValue(this.getLabelType());
//                        adminProc.setObjectBeforeValue(label.getLabelType());
//                        adminProc.setDescription("label.proc.update.labelType");
                    }
                    case "labelRules" -> {//修改标签规则
                        WrapperUtils.setPropertyValue(updateWrapper, Label::getLabelRules, JSON.toJSONString(this.getLabelRules()));
//                        adminProc.setObjectValue(this.getLabelRules());
//                        adminProc.setObjectBeforeValue(label.getLabelRules());
//                        adminProc.setDescription("label.proc.update.labelRules");
                    }
                }
            }
        }
    }
}
