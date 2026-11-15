package com.console.api.service;

import com.console.api.dto.req.ReceiveRewardReq;
import com.console.api.dto.res.InviteNewUserRewardsRes;

import java.util.concurrent.CompletableFuture;

public interface ActivityAPIService {
    Object getActivityList();
    InviteNewUserRewardsRes getInviteNewUserRewards();
    Object receiveInviteReward(ReceiveRewardReq receiveRewardReq);
    Object receiveActivityReward(ReceiveRewardReq receiveRewardReq);
    Object checkIntroduction();
    Object checkIn();
    CompletableFuture<Object> redeeming(String redeemCode);
}
