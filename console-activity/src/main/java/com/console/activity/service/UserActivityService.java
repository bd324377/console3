package com.console.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.entity.UserActivity;

public interface UserActivityService extends IService<UserActivity> {
    UserActivity getLastUserActivity(Integer userId,Integer activityId);
    UserActivity verifyUserActivity(UserActivity userActivity, Integer userId, ActivityDetails activityDetails);
}
