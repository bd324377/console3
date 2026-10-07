package com.console.user.service.impl;

import com.console.core.service.impl.BaseServiceImpl;
import com.console.user.entity.UserShuntConfig;
import com.console.user.mapper.UserShuntConfigMapper;
import com.console.user.service.UserShuntConfigService;
import org.springframework.stereotype.Service;

@Service
public class UserShuntConfigServiceImpl extends BaseServiceImpl<UserShuntConfigMapper,UserShuntConfig> implements UserShuntConfigService {
}
