package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.activity.dto.res.ActivityDetails;
import com.console.activity.entity.UserActivity;
import com.console.activity.mapper.UserActivityMapper;
import com.console.activity.service.UserActivityService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserActivityServiceImpl extends BaseServiceImpl<UserActivityMapper, UserActivity> implements UserActivityService {
    public UserActivity getLastUserActivity(Integer userId,Integer activityId) {
        QueryWrapper<UserActivity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserActivity::getUserId,userId)
                .eq(UserActivity::getActivityId,activityId)
                .orderByDesc(UserActivity::getId).last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    public UserActivity verifyUserActivity(UserActivity userActivity, Integer userId, ActivityDetails activityDetails) {
        if (userActivity == null || userActivity.getExpireTime().isBefore(LocalDateTime.now())) {//上次参与活动情况已完成则继续触发新一轮活动
            Integer round = userActivity == null ? 1 : userActivity.getRound() + 1;
            userActivity = new UserActivity(userId,activityDetails.getId(),null);
            userActivity.setRound(round);
            userActivity.setExpireTime(activityDetails.getCycle() == 0 ? activityDetails.getEndTime() : LocalDate.now().atStartOfDay().plusDays(activityDetails.getCycle()));
        } else {
            if (userActivity.getStatus() == 1) return null;
        }
        return userActivity;
    }
}
