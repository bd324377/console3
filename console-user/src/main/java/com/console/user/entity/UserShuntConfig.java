package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("b_user_shunt_config")
public class UserShuntConfig implements Serializable {//用户分流配置

    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer layers;//分流规则层级
    private Integer shuntStatus;//默认分流状态
    private BigDecimal shuntRate;//默认分流率
    private Integer shuntThreshold;//默认分流阈值
    private Integer updateStatus;//修改状态；0、未修改；1、已修改（单独修改后上级修改不再对其生效）
    private LocalDateTime updateTime;//更新时间
}
