package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName("s_picture")
public class Picture implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图片主键id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String picName;
    private String picPath;
}
