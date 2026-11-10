package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.activity.dto.req.ActivityReq;
import com.console.activity.entity.Activity;
import com.console.activity.entity.ActivityPrize;
import com.console.activity.entity.ActivityRule;
import com.console.activity.entity.ActivityRuleItem;
import com.console.activity.entity.Prize;
import com.console.activity.mapper.ActivityMapper;
import com.console.activity.service.ActivityPrizeService;
import com.console.activity.service.ActivityRuleItemService;
import com.console.activity.service.ActivityRuleService;
import com.console.activity.service.ActivityService;
import com.console.activity.service.PrizeService;
import com.console.core.entity.AdminProc;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestBackendUser;
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
    @Resource
    private ActivityPrizeService activityPrizeService;
    @Resource
    private PrizeService prizeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addActivity(ActivityReq.AddActivityReq addActivityReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        Activity activity = addActivityReq.buildActivity();
        if (activity.getMarketSiteId() == null) {
            activity.setMarketSiteId(requestBackendUser.getMarketSiteId());
        }
        validateTime(activity.getStartTime(), activity.getEndTime());
        if (this.save(activity)) {
            List<ActivityRule> activityRules = addActivityReq.buildActivityRules(activity.getId());
            if (!ObjectUtils.isEmpty(activityRules)) {
                if (!activityRuleService.saveBatch(activityRules)) {
                    throw new BusinessException(500, "新增活动规则失败");
                }
                List<ActivityRuleItem> activityRuleItems = addActivityReq.buildActivityRuleItems(activityRules);
                List<ActivityPrize> activityPrizes = addActivityReq.buildActivityPirzes(activityRules);
                for (ActivityPrize activityPrize : activityPrizes) {
                    activityPrize.setSurpCount(activityPrize.getCount());
                }
                if ((!activityRuleItems.isEmpty() && !activityRuleItemService.saveBatch(activityRuleItems))
                        || (!activityPrizes.isEmpty() && !activityPrizeService.saveBatch(activityPrizes))) {
                    throw new BusinessException(500, "新增活动规则明细或奖励失败");
                }
            }
            adminProcMapper.insert(new AdminProc(requestBackendUser, AdminProc.ProcBelong.ACTIVITY,
                    AdminProc.ProcType.ADD, activity.getId(), null, activity));
            return true;
        }
        throw new BusinessException(500,"新增活动失败，请稍后重试");
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
        activityPrizeService.lambdaUpdate().eq(ActivityPrize::getActivityId, activityId).remove();
        activityRuleItemService.lambdaUpdate().eq(ActivityRuleItem::getActivityId, activityId).remove();
        activityRuleService.lambdaUpdate().eq(ActivityRule::getActivityId, activityId).remove();
        if (!this.removeById(activityId)) {
            throw new BusinessException(409, "活动已变更，请刷新后重试");
        }
        adminProcMapper.insert(new AdminProc(requestBackendUser, AdminProc.ProcBelong.ACTIVITY,
                AdminProc.ProcType.DELETE, activityId, activity, null));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateActivity(String field, ActivityReq.UpdateActivityReq updateActivityReq) {
        RequestBackendUser requestBackendUser = RequestUtils.getRequestBackendUser();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        if (field.equals("activityTime")) {
            validateTime(updateActivityReq.getStartTime(), updateActivityReq.getEndTime());
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
            queryWrapper.lambda().eq(Activity::getId, updateActivityReq.getId());
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
        return this.lambdaQuery().orderByDesc(Activity::getId).page(page);
    }

    @Override
    public List<Activity> getActivityList(Integer marketSiteId) {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,activity_name,blurb,icon,cycle_reward_times,reward_wallet_type,activity_type," +
                        "start_time,end_time,route_path,is_hot")
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

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new BusinessException(400, "活动开始时间必须早于结束时间");
        }
    }
}
