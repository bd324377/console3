package com.console.activity.service.impl;

import com.console.activity.entity.UserActivity;
import com.console.activity.mapper.UserActivityMapper;
import com.console.activity.service.UserActivityService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserActivityServiceImpl extends BaseServiceImpl<UserActivityMapper, UserActivity> implements UserActivityService {
}
