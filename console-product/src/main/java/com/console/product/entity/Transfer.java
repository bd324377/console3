package com.console.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_transfer")
public class Transfer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 中转服务信息记录主键id
     */
    @TableId("id")
    private Integer id;
    private Long productPlatformMaxId;
    private Long productMaxId;
    private Long productOrderMaxId;
}
