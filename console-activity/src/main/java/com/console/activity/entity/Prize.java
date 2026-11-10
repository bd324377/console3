package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.console.framework.domain.LanguagePack;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@TableName(value = "s_prize",autoResultMap = true)
public class Prize implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 奖品id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String  prizeName;//奖品名称
    private String  blurb;//奖品介绍
    private String  icon;//奖品图片
    private Integer type;//奖品类型 1、实物；	2、现金奖励；	3、充值奖励卡；	4、提现手续费减免卡；；6、抽奖次数
    private String  remarks;//备注
}
