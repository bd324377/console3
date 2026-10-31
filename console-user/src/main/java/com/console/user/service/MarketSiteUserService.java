package com.console.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.user.entity.MarketSiteUser;
import com.console.user.entity.User;

public interface MarketSiteUserService extends IService<MarketSiteUser> {
    MarketSiteUser initUserId(User user);
}
