package com.console.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.user.entity.UserReport;

public interface UserReportMapper extends BaseMapper<UserReport> {
    /**查询直属下级充值和下单报告**/
    UserReport selectSubUserOrderAndRechargeReport(Integer parentId);
}
