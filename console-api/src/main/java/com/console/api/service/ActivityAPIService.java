package com.console.api.service;

import com.console.api.dto.res.InviteNewUserRewardsRes;

import java.util.List;

public interface ActivityAPIService {
    Object getActivityList();
    InviteNewUserRewardsRes getInviteNewUserRewards();
}
