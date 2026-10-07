package com.console.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("s_error_record")
public class ErrorRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;         //异常主键ID
    private Integer type;       //异常类型
    private String  tenantCode; //出现异常的租户编码
    private String  objectName; //出现异常的对象名称
    private String  objectId;   //对象ID
    private String  errorClass; //错误所出现类
    private String  errorMethod;//错误发生的方法
    private String  stackTrace; //堆栈信息
    private String  errorDesc;  //异常描述
    private Integer errorTime;  //错误发生时间
}
