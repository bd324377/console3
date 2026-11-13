package com.console.api.dto.req;

import com.console.framework.request.RequestMember;
import com.console.payment.entity.BankCard;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class BankCardReq {
    @Getter
    @Setter
    public static class AddBankCardReq {
        private String phoneNum;        //手机号
        private String bankCardCode;    //银行卡号
        private String emailCode;       //邮箱
        private String realName;        //真实姓名

        public BankCard buildBankCard(BankCard.BankCardType bankCardType,RequestMember requestMember) {
            BankCard bankCard = new BankCard();
            bankCard.setType(bankCardType);
            bankCard.setBankCardCode(bankCardType.getKey() == 1 ? bankCardCode : bankCardType.getKey() == 2 ? phoneNum : emailCode);
            bankCard.setPhone(phoneNum);
            bankCard.setUserId(requestMember.getId());
            bankCard.setBindTime(LocalDateTime.now());
            bankCard.setStatus(BankCard.BankCardStatus.PENDING);
            bankCard.setUserName(realName);
            return bankCard;
        }
    }
}
