package com.console.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.product.entity.PlatformBelong;

public interface PlatformBelongService extends IService<PlatformBelong> {
    boolean updatePlatformBelongStatus(PlatformBelong platformBelong);
    PlatformBelong getOpenPlatformBelong(Integer tenantId);
}
