package com.console.api.service;

import com.console.api.dto.req.ReceiveInviteRewardReq;
import com.console.api.dto.res.InviteNewUserRewardsRes;

import java.util.concurrent.CompletableFuture;

public interface ActivityAPIService {
    Object getActivityList();
    InviteNewUserRewardsRes getInviteNewUserRewards();
    Object receiveInviteReward(ReceiveInviteRewardReq receiveInviteRewardReq);
    Object checkIntroduction();
    Object checkIn();
    CompletableFuture<Object> redeeming(String redeemCode);
}
