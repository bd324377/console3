package com.console.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.console.core.entity.Wallet;
import org.apache.ibatis.annotations.Param;

public interface WalletMapper extends BaseMapper<Wallet> {
    Wallet selectSubUserWalletReport(@Param("parentId") Integer parentId);
}
