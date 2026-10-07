package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author aha
 * @since 2024-09-24
 */
@Getter
@Setter
@TableName("r_agent_layer")
public class AgentLayer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private Integer userId;//用户id
    private Integer supUserId;//用户上级id
    private Integer layers;//用户上级所属层级数
    private Integer layerStatus;//层级关系状态 1、正常邀新，0、分流掉链

    @TableField(exist = false)
    private Integer subUserCount;//直属下级用户数量
}
