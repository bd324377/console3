package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_platform_belong")
public class PlatformBelong implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;
    private String  belongName;
    private Integer status;     //1、是 0、否

    @Getter
    public enum Belong {
        OFFICIAL(1,"官方"),
        AG(2,"AG");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String desc;

        Belong(Integer key,String desc) {
            this.key  = key;
            this.desc = desc;
        }
    }
}
