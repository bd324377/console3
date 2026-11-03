package com.console.product.model.req;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.core.model.req.BaseUpdateReq;
import com.console.framework.domain.IUpdateField;
import com.console.product.entity.OrderMode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

public class OrderModeReq {
    @Getter
    @Setter
    public static class UpdateOrderModeReq extends BaseUpdateReq<OrderMode> implements Serializable {
        private String  modeName;//模式名称
        private List<OrderMode.MerchantSwitchRule> switchRules;//模式对应的厂商商户列表，最少一个
        @Getter
        public enum UpdateField implements IUpdateField<OrderMode> {
            ORDER_NAME("modeName",OrderMode::getModeName,"orderMode.proc.update.modeName"),
            SWITCH_RULES("switchRules",OrderMode::getSwitchRules,"orderMode.proc.update.switchRules");
            private final String fieldName;
            private final SFunction<OrderMode, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName, SFunction<OrderMode, Object> function, String descriptionKey) {
                this.fieldName = fieldName;
                this.function = function;
                this.descriptionKey = descriptionKey;
            }
        }
        @Override
        protected SFunction<OrderMode, ?> idFunction() {
            return OrderMode::getId;
        }

        @Override
        protected AdminProc.ProcBelong procBelong() {
            return AdminProc.ProcBelong.PRODUCT_ORDER_MODE;
        }

        @Override
        protected IUpdateField<OrderMode> parseField(String fieldName) {
            return IUpdateField.resolve(UpdateField.class,fieldName);
        }
    }
}
