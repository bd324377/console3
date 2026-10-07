package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
@Getter
@Setter
@TableName("r_user_relation")
public class UserRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @NotNull(groups = ValidationGroup.update.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;//关系主键id
    private Integer userId;//用户ID
    @NotNull(groups = ValidationGroup.insert.class)
    private Integer relationUserId;//关系用户ID
    private Integer relationState;//关系状态：0、等待对方确认；1、关系成立
    private Integer shareOrderState;//是否分享下单情况 1、是；0、否
    private Integer shareActivityState;//是否分享活动情况：1、是；0、否
}
