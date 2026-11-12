package com.console.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.activity.entity.UserPrize;
import com.console.activity.mapper.UserPrizeMapper;
import com.console.activity.service.UserPrizeService;
import com.console.core.entity.Wallet;
import com.console.core.entity.WalletProc;
import com.console.core.service.WalletService;
import com.console.core.service.impl.BaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserPrizeServiceImpl extends BaseServiceImpl<UserPrizeMapper, UserPrize> implements UserPrizeService {
    @Resource
    private WalletService walletService;
    @Override
    public WalletProc receiveAmountReward(UserPrize userPrize) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("balance,cash_able_amt,to_be_order_amt")
                .lambda().eq(Wallet::getUserId,userPrize.getUserId()).eq(Wallet::getWalletType,userPrize.getWalletType());
        Wallet wallet = walletService.getOne(queryWrapper);
        BigDecimal awardAmt = new BigDecimal(userPrize.getPrizeValue());
        UpdateWrapper<Wallet> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("balance = balance + " + awardAmt).lambda().eq(Wallet::getUserId,userPrize.getUserId()).eq(Wallet::getWalletType,userPrize.getWalletType());
        wallet.setBalance(wallet.getBalance().add(awardAmt));
        if (userPrize.getWalletType() == Wallet.WalletType.USER_WALLET) {//用户钱包
            if (userPrize.getToBeOrderAmt() == null || userPrize.getToBeOrderAmt().compareTo(BigDecimal.ZERO) <= 0) {
                updateWrapper.setSql("cash_able_amt = cash_able_amt + " + awardAmt);
                wallet.setCashAbleAmt(wallet.getCashAbleAmt().add(awardAmt));
            } else {
                updateWrapper.setSql("to_be_order_amt = to_be_order_amt + " + userPrize.getToBeOrderAmt());
                wallet.setToBeOrderAmt(wallet.getToBeOrderAmt().add(userPrize.getToBeOrderAmt()));
            }
        }
        if (walletService.update(updateWrapper) && this.save(userPrize)) {//用户奖励保存成功，钱包金额更新成功
            return new WalletProc(null,userPrize.getUserId(),userPrize.getWalletType(),1,
                    WalletProc.ProcType.ACTIVITY_REWARD,
                    wallet.getBalance(), awardAmt, userPrize.getToBeOrderAmt());
        }
        return null;
    }
}
