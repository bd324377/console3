package com.console.activity.mapper;

import com.console.activity.task.model.InviteNewSettlementParam;
import com.console.activity.task.model.VipUpgradeSettlementParam;
import com.console.core.entity.UserProc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysActivitySettlementMapper {
    UserProc selectActivateNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);//查询激活情况
    List<InviteNewSettlementParam> getInviteNewSettlementParams(@Param("fromId") Long fromId, @Param("toId") Long toId);//邀新结算参数查询
    /////////////////////////VIP升级////////////////////////////////////////
    UserProc selectVipUpgradeNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);//查询用户VIP升级情况
    List<VipUpgradeSettlementParam> getVipUpgradeSettlementParams(@Param("fromId") Long fromId, @Param("toId") Long toId);//VIP升级结算
    /////////////////////////代理薪资结算////////////////////////////////////////
    UserProc selectAgentSalaryNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);//查询用户VIP升级情况
    List<VipUpgradeSettlementParam> getAgentSalarySettlementParams(@Param("fromId") Long fromId, @Param("toId") Long toId);//VIP升级结算
    /////////////////////////经纪人薪资结算////////////////////////////////////////
    UserProc selectBrokerSalaryNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);//查询用户VIP升级情况
    List<VipUpgradeSettlementParam> getBrokerSalarySettlementParams(@Param("fromId") Long fromId, @Param("toId") Long toId);//VIP升级结算
    /////////////////////////订单返佣结算////////////////////////////////////////
    UserProc selectOrderRakeBackNewestProc(@Param("settledProcId") Long settledProcId, @Param("limitSize") Integer limitSize);//查询用户VIP升级情况
    List<VipUpgradeSettlementParam> getOrderRakeBackSettlementParams(@Param("fromId") Long fromId, @Param("toId") Long toId);//VIP升级结算
}
