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
@TableName("b_user_rake_back_config")
public class UserRakeBackConfig implements Serializable {//用户返佣配置
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer layers;//返佣规则层级
    private Integer rakeBackStatus;//返佣状态
    private Integer rakeBackRange;//返佣范围
    private BigDecimal rakeBackRate;//返佣率
    private Integer updateStatus;//单独修改状态；0、未修改；1、已修改（单独修改后上级修改不再对其生效）
    private LocalDateTime updateTime;//修改时间
}
