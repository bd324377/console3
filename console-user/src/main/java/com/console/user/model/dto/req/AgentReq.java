package com.console.user.model.dto.req;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.console.core.entity.AdminProc;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.WrapperUtils;
import com.console.user.entity.Agent;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class AgentReq {
    @Getter
    @Setter
    public static class UpdateAgentReq implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private UpdateWrapper<Agent> updateWrapper;
        private AdminProc adminProc;
        private String errorDesc;

        private Long id;
        private String agentName;
        private String icon;
        private Integer level;
        private BigDecimal upgradeAmt;
        private BigDecimal upgradeOrderAmt;
        private Integer cashOutStatus;//是否能提现：0、禁止提现1、自动提现；2、审核提现
        private BigDecimal cashOutRate;//提现手续费率
        private BigDecimal dayCashOutLimit;//日提现限额
        private BigDecimal transferOrderRate;//转账到用户钱包需下单比例
        private Integer rakeBackState;//返佣状态
        private Integer rakeBackRange;//返佣范围（正数为充值返佣，负数为订单返佣）  -1、订单返佣；0、每笔都有奖励；1、只有首充有奖励；2、只有前两笔充值有奖励；3、每日的前N比充值有奖励
        private Integer dailyRakeBackNum;//每日返佣笔数(返佣范围为3时启用该参数)
        private List<Agent.RakeBackRate> rakeBackRates;//返佣率

        @Getter
        public enum UpdateField {
            AGENT_LEVEL_NAME("agentName",Agent::getAgentName, "vip.proc.update.vipName"),
            AGENT_ICON("icon",Agent::getIcon, "vip.proc.update.welfareDesc");

            private final String fieldName;
            private final SFunction<Agent, Object> function;
            private final String descriptionKey;

            UpdateField(String fieldName,SFunction<Agent, Object> function, String descriptionKey) {
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
                throw new IllegalArgumentException("Invalid key: " + fieldName);
            }
        }
        /**
         * 创建更新代理等级信息的参数和过程
         * @param part  更新资源名
         * @param agent 更新前信息
         */
        public void buildUpdateParam(String part, Agent agent) {
            if (agent == null) {
                return;
            }
            UpdateField updateField = UpdateField.getUpdateFieldByField(part);
            RequestBackendUser backendUser = RequestUtils.getRequestBackendUser();
            adminProc = new AdminProc();
            updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(Agent::getId, agent.getId());

            // 统一设置字段
            Object newValue = FieldUtils.getFieldValue(this, updateField.getFieldName());
            SFunction<Agent, Object> function = updateField.getFunction();
            Object oldValue = function.apply(agent);
            WrapperUtils.setPropertyValue(updateWrapper, function, newValue);
            adminProc = new AdminProc(backendUser, AdminProc.ProcBelong.TENANT, AdminProc.ProcType.UPDATE, null, oldValue,newValue);
        }
    }
}
