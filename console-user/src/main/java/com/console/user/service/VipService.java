package com.console.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.user.entity.Vip;
import com.console.user.model.bo.VipBo;

import java.util.List;

public interface VipService extends IService<Vip> {
    Integer getInitialVipId();//获取初始vip等级ID
    VipBo getVipById(Integer vipId);//获取指定Vip信息
    List<Vip> getVipList();
}
