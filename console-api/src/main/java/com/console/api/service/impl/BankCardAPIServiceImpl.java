package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.api.dto.req.BankCardReq;
import com.console.api.service.BankCardAPIService;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.payment.entity.BankCard;
import com.console.payment.service.BankCardService;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankCardAPIServiceImpl extends BaseServiceImpl implements BankCardAPIService {
    @Resource
    private BankCardService bankCardService;
    /**
     * 获取已绑定银行卡列表
     */
    @Override
    public List<BankCard> getBankCardBankList() {
        QueryWrapper<BankCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BankCard::getUserId, RequestUtils.getRequestMember().getId());
        return bankCardService.list(queryWrapper);
    }

    /**
     * 新增银行卡信息（手机）
     * @param addBankCardReq    新增银行卡信息
     */
    @Override
    public boolean addUserPhoneBankCard(BankCardReq.AddBankCardReq addBankCardReq) {
        try {
            RequestMember requestMember = RequestUtils.getRequestMember();
            QueryWrapper<BankCard> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(BankCard::getBankCardCode, addBankCardReq.getBankCardCode())
                    .eq(BankCard::getType,BankCard.BankCardType.PHONE.getKey())
                    .eq(BankCard::getUserId,requestMember.getId());
            long count = bankCardService.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(500, I18nUtil.getI8nMsg("bankCard.error.add.userHadBankCard"));
            }
            BankCard bankCard = addBankCardReq.buildBankCard(BankCard.BankCardType.PHONE,requestMember);
            return bankCardService.save(bankCard);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("bankCard.error.add.sameBankCardCode"));
        }
    }

    /**
     * 新增银行卡信息（邮箱）
     * @param addBankCardReq    新增银行卡信息
     */
    @Override
    public boolean addUserEmailBankCard(BankCardReq.AddBankCardReq addBankCardReq) {
        try {
            RequestMember requestMember = RequestUtils.getRequestMember();
            QueryWrapper<BankCard> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(BankCard::getBankCardCode, addBankCardReq.getBankCardCode())
                    .eq(BankCard::getType,BankCard.BankCardType.EMAIL.getKey())
                    .eq(BankCard::getUserId,requestMember.getId());
            long count = bankCardService.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("bankCard.error.add.userHadBankCard"));
            }
            BankCard bankCard = addBankCardReq.buildBankCard(BankCard.BankCardType.EMAIL,requestMember);
            return bankCardService.save(bankCard);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("bankCard.error.add.sameBankCardCode"));
        }
    }

    /**
     * 新增银行卡信息
     * @param addBankCardReq    新增银行卡信息
     */
    @Override
    public boolean addUserBankCard(BankCardReq.AddBankCardReq addBankCardReq) {
        try {
            RequestMember requestMember = RequestUtils.getRequestMember();
            QueryWrapper<BankCard> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(BankCard::getBankCardCode, addBankCardReq.getBankCardCode())
                    .eq(BankCard::getType,BankCard.BankCardType.CPF.getKey())
                    .eq(BankCard::getUserId,requestMember.getId());
            long count = bankCardService.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("bankCard.error.add.userHadBankCard"));
            }
            BankCard bankCard = addBankCardReq.buildBankCard(BankCard.BankCardType.CPF,requestMember);
            return bankCardService.save(bankCard);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("bankCard.error.add.sameBankCardCode"));
        }
    }
}
