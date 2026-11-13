package com.console.api.dto.res;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubUserRes {
    private Integer id;
    private Integer layers;
    private LocalDateTime loginTime;
    private LocalDateTime regTime;
    private Integer status;
    private BigDecimal rechargeAmt;
    private BigDecimal orderAmt;
    private Integer vipId;
    private Integer vipLevel;
}
