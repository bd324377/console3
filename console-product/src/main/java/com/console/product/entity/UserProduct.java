package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("r_user_product")
public class UserProduct implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Long productId;
    private String productName;
    private Type type;
    private LocalDateTime updateTime;

    @Getter
    public enum Type {

        UNKNOWN(0,"未知"),
        COLLECT(1,"收藏"),
        HISTORY(2,"最近");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        Type(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
