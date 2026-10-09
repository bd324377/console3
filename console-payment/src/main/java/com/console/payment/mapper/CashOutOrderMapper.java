package com.console.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.payment.entity.CashOutOrder;

public interface CashOutOrderMapper extends BaseMapper<CashOutOrder> {
    @org.apache.ibatis.annotations.Select("SELECT * FROM s_cash_out_order WHERE id = #{id} FOR UPDATE")
    CashOutOrder lockById(Long id);
}
