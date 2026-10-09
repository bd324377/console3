package com.console.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.payment.entity.PaymentOrder;

public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
    @org.apache.ibatis.annotations.Select("SELECT * FROM s_payment_order WHERE id = #{id} FOR UPDATE")
    PaymentOrder lockById(Long id);
}
