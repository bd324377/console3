package com.console.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.core.entity.Wallet;

public interface WalletMapper extends BaseMapper<Wallet> {
    @org.apache.ibatis.annotations.Select("SELECT * FROM s_user WHERE id = #{id} FOR UPDATE")
    Wallet lockById(Integer id);
}
