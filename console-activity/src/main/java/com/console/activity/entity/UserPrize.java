package com.console.activity.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.console.core.entity.Wallet;
import com.console.framework.constants.PrizeType;
import com.console.framework.domain.LanguagePack;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_user_prize")
public class UserPrize implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;//用户d
    private String prizeIcon;//奖励图标
    private PrizeType prizeType;//奖品类型
    private String prizeValue;//奖品值
    private Integer status;
    private LocalDateTime addTime;//领取时间
    private LocalDateTime expireTime;//过期时间
    private Integer activityId;//奖励源id（活动则表活动ID，抽奖表奖池ID，升级表vipID）

    @TableField(exist = false)
    private Wallet.WalletType walletType;//进入钱包类型
    @TableField(exist = false)
    private BigDecimal toBeOrderAmt;//待下单金额
}
