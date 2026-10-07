package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_user")
public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id")
    private Integer id;

    private Integer orderMode;
    private Integer subUserOrderMode;
}
