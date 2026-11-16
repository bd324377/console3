package com.console.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_user_report_period_agg")
public class UserReportPeriodAgg implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                                  // 主键id
    private Integer marketSiteId;                     // 运营站点ID
    private Integer marketTeamId;                     // 运营团队ID
    private Integer marketerId;                       // 运营业务员ID
    private Integer periodType;                       // 周期类型：1今日 2昨日 3本周 4上周 5本月 6上月
    private LocalDate periodStart;                    // 周期起始日期
    private LocalDate periodEnd;                      // 周期结束日期
    private Integer registerUserCount;                // 注册人数
    private Integer loginUserCount;                   // 登录人数
    private Integer firstRechargeUserCount;           // 首充人数
    private BigDecimal firstRechargeAmt;              // 首充金额
    private Integer rechargeUserCount;                // 充值人数
    private Long rechargeTimes;                       // 充值次数
    private BigDecimal rechargeAmt;                   // 充值金额
    private Integer cashOutUserCount;                 // 提现人数
    private Long cashOutTimes;                        // 提现次数
    private BigDecimal cashOutAmt;                    // 提现金额
    private Integer orderUserCount;                   // 下单人数
    private BigDecimal orderAmt;                      // 下单金额
    private BigDecimal orderResultAmt;                // 订单盈亏金额
    private Integer aggVersion;                       // 每次重算递增，用于校验
    private LocalDateTime updateTime;                 // 更新时间
}
