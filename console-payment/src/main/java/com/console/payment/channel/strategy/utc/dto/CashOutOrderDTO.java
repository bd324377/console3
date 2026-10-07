package com.console.payment.channel.strategy.utc.dto;

import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentChannel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashOutOrderDTO {
    private String  merchantId;      //商户ID
    private String  merchantOrderNo; // 商户订单号
    private Integer amount;          // 金额(单位:分)
    private String  currency;        // 币种
    private String  accountType;     // 账户类型
    private String  bankCode;        // 银行编码（可选）
    private String  branchBankNo;    // 支行行号（可选）
    private String  branchBankName;  // KYC付款人名称（可选）
    private String  accountNo;       // 账号
    private String  accountName;     //账户名
    private String  accountMobile;   //账户手机号
    private String  accountEmail;    //账户邮箱
    private String  province;        //省份
    private String  city;            //城市
    private String  cpf;             //CPF/CPF_CNPJ
    private String  ifsc;            //IFSC
    private String  callback;        // 回调地址
    private String  sign;            // 签名

    public CashOutOrderDTO() {}

    public CashOutOrderDTO(CashOutOrder cashOutOrder, PaymentChannel channel, BankCard bankCard, String serviceCode) {
//        BigDecimal cashOutAmt = cashOutOrder.getOrderAmt();
//        if (cashOutOrder.getFee() != null && cashOutOrder.getFee().compareTo(BigDecimal.ZERO) > 0) {
//            cashOutAmt = cashOutAmt.subtract(cashOutOrder.getFee());
//        }
//        this.merchantId      = channel.getAppKey();
//        this.merchantOrderNo = serviceCode + "_" + TenantUtils.getTenantCode() + "_" + cashOutOrder.getId().toString();
//        this.amount          = cashOutAmt.multiply(new BigDecimal("100")).intValue();
//        this.currency        = channel.getCurType();
//        this.accountType     = bankCard.buildBankCardAccountTypeOfBrazil();
//        this.accountNo       = bankCard.getBankCardCode();
//        this.accountName     = bankCard.getUserName();
//        this.callback        = RequestUtils.getRequestPrefix() + channel.getCashOutBackUrl();
        buildSign(channel.getApiSecret());
    }

    public void buildSign(String apiSecret) {
//        TreeMap<String, Object> paramsMap = PaymentUtils.convert(this, true, false);
//        this.sign = PaymentUtils.serialMd5Sign(paramsMap,apiSecret);
    }
}
