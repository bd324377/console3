package com.console.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.user.entity.UserId;

public interface UserIdService extends IService<UserId> {
    Integer initUserId();
}
