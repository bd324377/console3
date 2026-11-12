package com.console.activity.mapper;

import com.console.activity.dto.bo.UserRechargeAndOrderAmt;

import java.time.LocalDate;

public interface ActivityTriggerMapper {
    UserRechargeAndOrderAmt selectTotalRechargeAndOrderAmt(LocalDate start, LocalDate end);
}
