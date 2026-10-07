package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.console.framework.constants.ValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 头像表实体类
 */
@Getter
@Setter
@TableName("s_avatar")
public class Avatar implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @NotNull(groups = ValidationGroup.update.class)
    private Integer id;
    private Integer sex;    //性别 1、男；0、女
    @NotBlank(groups = ValidationGroup.insert.class)
    private String avatarPath;//头像地址
    private String avatarName;//头像名称
}
