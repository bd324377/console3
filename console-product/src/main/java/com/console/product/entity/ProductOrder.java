package com.console.product.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_product_order")
public class ProductOrder implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;//主键id
    private String roundId;//回合ID
    @JSONField(name = "betId")
    private String orderId;//订单id
    @JSONField(name = "parentBetId")
    private String parentOrderId;//父订单id
    private Integer userId;
    @JSONField(name = "gameId")
    private Long productId;//产品id
    @JSONField(name = "platId")
    private Integer platformId;//产商id
    @JSONField(name = "gameCateId")
    private Product.ProductCategory category;//产品主分类
    @JSONField(name = "betAmount")
    private BigDecimal orderAmt;//订单金额
    @JSONField(name = "payoutAmount")
    private BigDecimal payoutAmt;//支出金额
    @JSONField(name = "winLossAmount")
    private BigDecimal resultAmt;//订单结果金额
    private Integer resultStatus;//盈利状态：1、盈利；2、亏损；3、持平
    private BigDecimal balance;//余额
    private BigDecimal beforeBalance;//下单前金额
    private LocalDate orderDate;//下单日期
    @JSONField(name = "betTime")
    private LocalDateTime orderTime;//下单时间
    @JSONField(name = "betEndTime")
    private LocalDateTime orderEndTime;//结单时间
    private Integer status;
    private Integer amtSource;//金额来源 0、正常账户；1、试玩账户
    @JSONField(name = "platformAccountType")
    private Integer userSuffix;//用户后缀
    @TableField(exist = false)
    private Integer tenantId;
    @TableField(exist = false)
    private Integer orderCount;//订单数量
}
