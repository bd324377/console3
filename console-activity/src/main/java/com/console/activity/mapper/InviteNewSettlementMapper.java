package com.console.activity.mapper;

import com.console.activity.task.handler.inviteNew.model.InviteNewSettlementParam;
import com.console.core.entity.UserProc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InviteNewSettlementMapper {
    UserProc selectNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);
    List<InviteNewSettlementParam> getInviteNewSettlementParams(@Param("fromId") Long fromId, @Param("toId") Long toId);
}
