package com.console.activity.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.activity.dto.req.ActivityReq;
import com.console.activity.dto.res.ActivityPageRes;
import com.console.activity.entity.Activity;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    boolean addActivity(ActivityReq.AddActivityReq addActivityReq);
    boolean delActivity(Integer activityId);
    boolean updateActivity(String field, ActivityReq.UpdateActivityReq updateActivityReq);
    Object getActivityPage(Page<Activity> page);
    List<Activity> getActivityList(Integer marketSiteId);
}
