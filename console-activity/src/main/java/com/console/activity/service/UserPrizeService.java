package com.console.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.activity.entity.UserPrize;
import com.console.core.entity.WalletProc;

public interface UserPrizeService extends IService<UserPrize> {
    WalletProc receiveAmountReward(UserPrize userPrize);
}
