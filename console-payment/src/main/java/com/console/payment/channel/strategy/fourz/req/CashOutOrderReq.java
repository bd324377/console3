package com.console.payment.channel.strategy.fourz.req;

import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TenantUtils;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.Channel;
import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CashOutOrderReq {
    private String  merchantNo;      //商户ID
    private String  merchantOrderNo; // 商户订单号
    private Integer amount;          // 金额(单位:分)
    private String  currency;        // 币种
    private String  accountType;     // 账户类型
    private String  bankCode;        // 银行编码（可选）
    private String  branch;          // 支行行号（可选）
    private String  branchName;      // 支行名称（可选）
    private String  account;         // 账号
    private String  accountName;     //账户名
    private String  phone;           //账户手机号
    private String  email;           //账户邮箱
    private String  province;        //省份
    private String  city;            //城市
    private String  cpf;             //CPF/CPF_CNPJ
    private String  ifsc;            //IFSC
    private String  callback;        // 回调地址
    private String  sign;            // 签名

    public CashOutOrderReq(CashOutOrder cashOutOrder, Channel channel, BankCard bankCard, String serviceCode) {
        BigDecimal cashOutAmt = cashOutOrder.getOrderAmt();
        if (cashOutOrder.getFee() != null && cashOutOrder.getFee().compareTo(BigDecimal.ZERO) > 0) {
            cashOutAmt = cashOutAmt.subtract(cashOutOrder.getFee());
        }
        this.merchantNo      = channel.getAppKey();
        this.merchantOrderNo = serviceCode + "_" + TenantUtils.getTenantId() + "_" + cashOutOrder.getMarketSiteId() + "_" + cashOutOrder.getId().toString();
        this.amount          = cashOutAmt.multiply(new BigDecimal("100")).intValue();
        this.currency        = channel.getCurType();
        this.accountType     = bankCard.getType().getValue();
        this.account         = bankCard.getBankCardCode();
        this.accountName     = bankCard.getUserName();
        this.callback        = RequestUtils.getRequestPrefix() + channel.getCashOutCallBackUrl();
        sign = PaymentUtils.sign(this, channel.getApiSecret());
    }
}
