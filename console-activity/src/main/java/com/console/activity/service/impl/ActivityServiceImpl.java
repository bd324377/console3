package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.activity.dto.req.ActivityReq;
import com.console.activity.entity.Activity;
import com.console.activity.entity.ActivityRule;
import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.mapper.ActivityMapper;
import com.console.activity.service.ActivityRuleItemService;
import com.console.activity.service.ActivityRuleService;
import com.console.activity.service.ActivityService;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.utils.FieldUtils;
import com.console.framework.utils.RequestUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl extends BaseServiceImpl<ActivityMapper, Activity> implements ActivityService {
    @Resource
    private ActivityRuleService activityRuleService;
    @Resource
    private ActivityRuleItemService activityRuleItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addActivity(ActivityReq.AddActivityReq addActivityReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        Activity activity = addActivityReq.buildActivity();
        if (this.save(activity)) {
            List<ActivityRule> activityRules = addActivityReq.buildActivityRules(activity.getId());
            if (!ObjectUtils.isEmpty(activityRules)) {
                if (activityRuleService.saveBatch(activityRules)) {
                    List<ActivityRuleItem> activityRuleItems = addActivityReq.buildActivityRuleItems(activityRules);
                    if (activityRuleItemService.saveBatch(activityRuleItems)) {
                        AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.ACTIVITY, AdminProc.ProcType.ADD,activity.getId(),null,activity);
                        adminProcMapper.insert(adminProc);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delActivity(Integer activityId) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,market_site_id").lambda().eq(Activity::getId, activityId);
        Activity activity = this.getOne(queryWrapper);
        if (activity == null) {
            throw new BusinessException(500,"删除活动失败，平台未找到该活动");
        }
        if (this.removeById(activityId)) {
            QueryWrapper<ActivityRule> ruleQueryWrapper = new QueryWrapper<>();
            ruleQueryWrapper.lambda().eq(ActivityRule::getActivityId,activityId);
            activityRuleService.remove(ruleQueryWrapper);

            QueryWrapper<ActivityRuleItem> ruleItemQueryWrapper = new QueryWrapper<>();
            ruleQueryWrapper.lambda().eq(ActivityRule::getActivityId,activityId);
            activityRuleItemService.remove(ruleItemQueryWrapper);

            AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.ACTIVITY, AdminProc.ProcType.DELETE,activity.getId(),activity,null);
            adminProcMapper.insert(adminProc);
            //清除缓存
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateActivity(String field, ActivityReq.UpdateActivityReq updateActivityReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        if (field.equals("activityTime")) {
            queryWrapper.select("id,start_time,end_time").lambda().eq(Activity::getId, updateActivityReq.getId());
            Activity activity = this.getOne(queryWrapper);
            if (activity == null) {
                throw new BusinessException(500,"待修改数据不存在，请确认后重试");
            }
            UpdateWrapper<Activity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(Activity::getId,updateActivityReq.getId())
                    .set(Activity::getStartTime,updateActivityReq.getStartTime())
                    .set(Activity::getEndTime,updateActivityReq.getEndTime());
            if (this.update(updateWrapper)) {
                AdminProc adminProc = new AdminProc(requestBackendUser, AdminProc.ProcBelong.ACTIVITY, AdminProc.ProcType.UPDATE,activity.getId(),activity,updateActivityReq);
                adminProcMapper.insert(adminProc);
                return true;
            }
        } else {
            queryWrapper.select("id", FieldUtils.convertToUnderscore(field)).lambda().eq(Activity::getId, updateActivityReq.getId());
            Activity activity = this.getOne(queryWrapper);
            updateActivityReq.buildUpdateParam(field, activity, requestBackendUser);
            if (this.update(updateActivityReq.getUpdateWrapper())) {
                adminProcMapper.insert(updateActivityReq.getAdminProc());
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getActivityPage(Page<Activity> page) {
        return this.page(page);
    }

    @Override
    public List<Activity> getActivityList(Integer marketSiteId) {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("activity_name,blurb,icon,cycle_reward_times,reward_wallet_type,activity_type,start_time,end_time,route_path,is_hot")
                .lambda()
                .eq(Activity::getMarketSiteId,marketSiteId)
                .eq(Activity::getActivityStatus,1)
                .lt(Activity::getStartTime, LocalDateTime.now())
                .gt(Activity::getEndTime, LocalDateTime.now())
                .orderByDesc(Activity::getSort);
        return this.list(queryWrapper);
    }

    private void clearActivityCache() {

    }
}
