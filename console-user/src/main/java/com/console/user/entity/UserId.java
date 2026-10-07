package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_user_id")
public class UserId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;//真实用户ID
    private Integer userId;//按位异或操作后的用户ID
    private Integer partitionCode;//分区记录 1~9

    @TableField(exist = false)
    private Integer retryTimes;
    @TableField(exist = false)
    private Boolean fromRecovery;
}
