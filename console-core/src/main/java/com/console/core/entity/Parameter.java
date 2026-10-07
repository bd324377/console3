package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_parameter")
public class Parameter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @NotNull(groups = ValidationGroup.update.class)
    private Integer id;
    private Integer marketTeamId;//团队id
    private String parameterCode;//参数编码
    private String parameterName;//参数名称
    private String value;//参数值
    private Integer isEnable;//是否开启 0、未开启；1、开启

    @Getter
    @Setter
    public static class RegLimitParameter {
        private Integer regLimitStatus;//是否允许注册开关
        private Integer regThreshold;//注册阈值，同IP/设备注册数达到阈值后不允许注册
        private Integer shuntStatus;//是否允许分流开关
        private Integer shuntThreshold;//分流阈值
        private Integer modifyOrderModeStatus;//是否修改下单模式
        private Integer modifyModeThreshold;//修改下单模式阈值
        private Integer orderModeId;//下单模式id
    }
}
