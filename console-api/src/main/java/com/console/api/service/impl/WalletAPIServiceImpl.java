package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.req.WalletReq;
import com.console.api.dto.res.WalletLogPageRes;
import com.console.api.service.WalletAPIService;
import com.console.core.entity.Wallet;
import com.console.core.entity.WalletProc;
import com.console.core.service.WalletProcService;
import com.console.core.service.WalletService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TokenUtils;
import com.console.payment.entity.WalletReport;
import com.console.payment.service.WalletReportService;
import com.console.user.entity.User;
import com.console.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class WalletAPIServiceImpl extends BaseServiceImpl implements WalletAPIService {
    @Resource
    private WalletService walletService;
    @Resource
    private UserService userService;
    @Resource
    private WalletProcService walletProcService;
    @Resource
    private WalletReportService walletReportService;

    @Override
    public Object getWalletBalance() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("balance,cash_able_amt,to_be_order_amt")
                .lambda().eq(Wallet::getUserId,requestMember.getId()).eq(Wallet::getWalletType, Wallet.WalletType.USER_WALLET);
        return walletService.getOne(queryWrapper);
    }

    @Override
    public Object getAgentWalletBalance() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("balance")
                .lambda().eq(Wallet::getUserId,requestMember.getId()).eq(Wallet::getWalletType, Wallet.WalletType.AGENT_WALLET);
        return walletService.getOne(queryWrapper);
    }

    @Override
    public boolean setCashOutPwd(String password) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        if (StringUtils.hasText(requestMember.getCashOutPwd())) {//已设置过密码，无需再重复设置
            return true;
        }
        DynamicTableNameHandler.setTenantId(requestMember.getTenantId());
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(User::getId,requestMember.getId()).set(User::getCashOutPwd,password);
        if (userService.update(updateWrapper)) {
            requestMember.setCashOutPwd(password);
            TokenUtils.updateUserToken(requestMember);
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyCashOutPwd(String password) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,cash_out_pwd").lambda().eq(User::getId, requestMember.getId());
        User user = userService.getOne(queryWrapper);
        return user.getCashOutPwd() != null && user.getCashOutPwd().equals(password);
    }

    @Override
    public Object getWalletLog(WalletReq.SearchWalletLogReq searchWalletLogReq) {
        Page<WalletProc> page = new Page<>(searchWalletLogReq.getCurrent(), searchWalletLogReq.getSize());
        QueryWrapper<WalletProc> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("type,proc_type,amt,to_be_order_amt,proc_time").lambda()
                .eq(WalletProc::getWalletType,searchWalletLogReq.getWalletType())
                .between(searchWalletLogReq.getStartSearchTime() != null && searchWalletLogReq.getEndSearchTime() != null,WalletProc::getProcTime, searchWalletLogReq.getStartSearchTime(),searchWalletLogReq.getEndSearchTime())
                .in(!ObjectUtils.isEmpty(searchWalletLogReq.getProcTypes()),WalletProc::getProcType,searchWalletLogReq.getProcTypes())
                .orderByDesc(WalletProc::getId);
        IPage<WalletProc> walletProcIPage = walletProcService.page(page,queryWrapper);
        WalletLogPageRes walletLogPageRes = new WalletLogPageRes(walletProcIPage);
        if (searchWalletLogReq.getCurrent() == 1 && walletProcIPage.getTotal() > 0) {
            QueryWrapper<WalletReport> reportQueryWrapper = new QueryWrapper<>();
            reportQueryWrapper.select("SUM(recharge_amt) as recharge_amt," +
                    "SUM(manual_recharge_amt) as manual_recharge_amt," +
                    "SUM(activity_reward_amt) as activity_reward_amt," +
                    "SUM(agent_wallet_transfer_amt) as agent_wallet_transfer_amt," +
                    "SUM(cash_out_amt) as cash_out_amt," +
                    "SUM(manual_cash_out_amt) as manual_cash_out_amt," +
                    "SUM(user_expend_amt) as user_expend_amt")
                    .lambda().eq(WalletReport::getWalletType,searchWalletLogReq.getWalletType())
                    .between(searchWalletLogReq.getStartSearchTime() != null && searchWalletLogReq.getEndSearchTime() != null,WalletReport::getReportDate,
                            searchWalletLogReq.getStartSearchTime().toLocalDate(),searchWalletLogReq.getEndSearchTime());
            WalletReport walletReport = walletReportService.getOne(reportQueryWrapper);
            if (walletReport != null) {
                walletLogPageRes.setWalletReport(walletReport);
            }
        }
        return walletLogPageRes;
    }

//    /**
//     * 分页获取用户钱包交易流水
//     * @param searchWalletLogReq  分页查询信息
//     */
//    private Map<String,Object> getUserWalletProcLog(WalletReq.SearchWalletLogReq searchWalletLogReq) {
//        RequestMember requestMember = RequestUtils.getRequestMember();
//        Map<String,Object> resultMap = new HashMap<>();
//        Page<WalletProc> page = new Page<>(searchWalletLogReq.getCurrent(),searchWalletLogReq.getSize());
//        searchWalletLogReq.setUserId(requestMember.getId());
//        IPage<UserWalletProc> userWalletProcIPage = walletApiMapper.selectUserWalletProcPage(page,searchWalletLogReq);
//        for (UserWalletProc userWalletProc : userWalletProcIPage.getRecords()) {
//            userWalletProc.setDescription(I18nUtil.getI8nMsg(userWalletProc.getDescription()));
//        }
//        QueryWrapper<UserWalletReport> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("SUM(online_recharge_amt) as online_recharge_amt," +
//                        "SUM(activity_reward_amt) as activity_reward_amt," +
//                        "SUM(online_cash_out_amt) as online_cash_out_amt," +
//                        "SUM(transfer_from_agent_wallet_amt) as transfer_from_agent_wallet_amt," +
//                        "SUM(transfer_from_provident_fund_amt) as transfer_from_provident_fund_amt," +
//                        "SUM(day_cash_back_amt) as day_cash_back_amt," +
//                        "SUM(week_cash_back_amt) as week_cash_back_amt").lambda()
//                .between(UserWalletReport::getReportDate,searchWalletLogReq.getStartSearchTime(),searchWalletLogReq.getEndSearchTime())
//                .eq(UserWalletReport::getUserId,searchWalletLogReq.getUserId());
//        UserWalletReport userWalletReport = userWalletReportMapper.selectOne(queryWrapper);
//        if (userWalletReport == null) {
//            userWalletReport = new UserWalletReport();
//            userWalletReport.setOnlineRechargeAmt(BigDecimal.ZERO);
//            userWalletReport.setActivityRewardAmt(BigDecimal.ZERO);
//            userWalletReport.setOnlineCashOutAmt(BigDecimal.ZERO);
//            userWalletReport.setTransferFromAgentWalletAmt(BigDecimal.ZERO);
//            userWalletReport.setTransferFromProvidentFundAmt(BigDecimal.ZERO);
//            userWalletReport.setDayCashBackAmt(BigDecimal.ZERO);
//            userWalletReport.setWeekCashBackAmt(BigDecimal.ZERO);
//        }
//        resultMap.put("userWalletReport",userWalletReport);
//        resultMap.put("ipage",userWalletProcIPage);
//        return resultMap;
//    }
//
//    /**
//     * 分页获取用户钱包交易流水
//     * @param searchWalletLogReq  分页查询信息
//     */
//    private Map<String,Object> getAgentWalletProcLog(WalletReq.SearchWalletLogReq searchWalletLogReq) {
//        Map<String,Object> resultMap = new HashMap<>();
//        Page<AgentWalletProc> page = new Page<>(walletLogDTO.getCurrent(),walletLogDTO.getSize());
//        walletLogDTO.setUserId(RequestUtils.getUser().getId());
//        IPage<AgentWalletProc> agentWalletProcIPage = walletApiMapper.selectAgentWalletProcPage(page,walletLogDTO);
//        for (AgentWalletProc agentWalletProc : agentWalletProcIPage.getRecords()) {
//            agentWalletProc.setDescription(I18nUtil.getI8nMsg(agentWalletProc.getDescription()));
//        }
//        QueryWrapper<AgentWalletReport> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("SUM(rake_back_amt) as rake_back_amt," +
//                        "SUM(activity_reward_amt) as activity_reward_amt," +
//                        "SUM(online_cash_out_amt) as online_cash_out_amt," +
//                        "SUM(transfer_to_user_wallet_amt) as transfer_to_user_wallet_amt").lambda()
//                .between(AgentWalletReport::getReportDate,walletLogDTO.getStartSearchTime(),walletLogDTO.getEndSearchTime())
//                .eq(AgentWalletReport::getInviterId,walletLogDTO.getUserId());
//        AgentWalletReport agentWalletReport = agentWalletReportMapper.selectOne(queryWrapper);
//        if (agentWalletReport == null) {
//            agentWalletReport = new AgentWalletReport();
//            agentWalletReport.setRakeBackAmt(BigDecimal.ZERO);
//            agentWalletReport.setActivityRewardAmt(BigDecimal.ZERO);
//            agentWalletReport.setOnlineCashOutAmt(BigDecimal.ZERO);
//            agentWalletReport.setTransferToUserWalletAmt(BigDecimal.ZERO);
//        }
//        resultMap.put("agentWalletReport",agentWalletReport);
//        resultMap.put("ipage",agentWalletProcIPage);
//        return resultMap;
//    }
}
