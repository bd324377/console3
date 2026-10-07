package com.console.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.user.entity.Vip;

public interface VipService extends IService<Vip> {
    Integer getInitialVipId();//获取初始vip等级ID
}
