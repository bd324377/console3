package com.console.user.mapper;

import com.console.core.entity.UserProc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserProcSettlementMapper {
    UserProc selectNewestUserProc(@Param("settledProcId") Long settledProcId,@Param("limitSize") Integer limitSize);
    void insertOrUpdateUserReport(@Param("fromId") Long fromId,@Param("toId") Long toId);
    void createTempPendingUserData(@Param("fromId") Long fromId,@Param("toId") Long toId);//创建待处理(VIP升级，代理升级，用户激活)用户临时表
    void updatePendingUserData();//更新结算期间有充值/下单用户的充值和下单情况
    void updateUserActivateRule();//更新用户激活规则
    void updateUserActivateStatus();//用户激活
    void insertActivateUserProc();//添加激活过程
    void updatePendingUserVipData();//更新待处理用户可升级VIP信息
    void vipUpgrade();//VIP升级
    void insertVipUpgradeUserProc();//添加用户升级记录
    void createTempAgentUpgradeData();//创建临时代理升级表
    void updateAgentSubUserData();//更新临时升级表中的直属下级充值和下单情况
    void updatePendingUserAgentData();//更新可升级代理等级
    void agentUpgrade();//代理等级升级
    void insertAgentUpgradeUserProc();//添加用户代理等级升级记录
    void truncateTempPendingUserData();//清空待处理用户临时表
    void truncateTempAgentUpgradeData();//清空代理升级临时表













    int ensureTaskState();
    Integer lockTaskState();
    List<Long> selectPendingIds(@Param("cutoff") long cutoff, @Param("limit") int limit);
    int insertBatch(@Param("batchId") String batchId, @Param("procCount") int procCount);
    int aggregateReports(@Param("ids") List<Long> ids);
    int insertReceipts(@Param("ids") List<Long> ids, @Param("batchId") String batchId);
    int completeBatch(@Param("batchId") String batchId);
    int completeTaskState(@Param("procCount") int procCount);
}
