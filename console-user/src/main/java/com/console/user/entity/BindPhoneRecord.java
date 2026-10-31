package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_bind_phone_record")
public class BindPhoneRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;//
    private Integer userId;
    private Integer marketSiteId;
    private String  phone;
    private LocalDateTime bindTime;

    @TableField(exist = false)
    private Integer bindCount;//绑定数量
}
