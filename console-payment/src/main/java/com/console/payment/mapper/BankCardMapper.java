package com.console.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.payment.entity.BankCard;

public interface BankCardMapper extends BaseMapper<BankCard> {
    @org.apache.ibatis.annotations.Select("SELECT * FROM s_bank_card WHERE id=#{id} FOR UPDATE")
    BankCard lockById(Long id);
}
