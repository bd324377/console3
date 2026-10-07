package com.console.payment.channel.strategy.u2c.dto.req;

import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentChannel;
import com.console.payment.utils.PaymentUtils;
import lombok.Getter;
import lombok.Setter;

/** U2C 巴西 PIX 提现请求。构造时完成账户类型规范化、字段校验和签名。 */
@Getter
@Setter
public class CashOutOrderReq {
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

    public CashOutOrderReq() {}

    public CashOutOrderReq(CashOutOrder cashOutOrder, PaymentChannel channel, BankCard bankCard, String serviceCode) {
        this.merchantId = channel.getAppKey();
        this.merchantOrderNo = cashOutOrder.getMerchantOrderNo();
        this.amount = PaymentUtils.toCents(cashOutOrder.getOrderAmt());
        this.currency = channel.getCurType();
        this.accountType = normalizeType(bankCard.getAccountType());
        this.bankCode = bankCard.getBankCode();
        this.branchBankNo = bankCard.getBranchBankNo();
        this.branchBankName = bankCard.getBranchBankName();
        this.accountNo = bankCard.getAccountNo();
        this.accountName = bankCard.getAccountName();
        this.accountMobile = bankCard.getAccountMobile();
        this.accountEmail = bankCard.getAccountEmail();
        this.province = bankCard.getProvince();
        this.city = bankCard.getCity();
        this.cpf = bankCard.getCpf();
        this.ifsc = bankCard.getIfsc();
        this.callback = channel.getCashOutCallBackUrl();
        validatePix();
        buildSign(channel.getApiSecret());
    }

    public void buildSign(String apiSecret) {
        this.sign = PaymentUtils.sign(this, apiSecret);
    }

    private String normalizeType(String value) {
        return "PIX_RANDOM".equals(value) ? "PIX_EVP" : value;
    }

    private void validatePix() {
        // 不同 PIX Key 类型的必填字段不同，在请求渠道前尽早失败。
        if (accountType == null || accountNo == null || accountNo.isBlank()
                || accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("PIX account type, number and account name are required");
        }
        switch (accountType) {
            case "PIX_PHONE" -> require(accountNo.startsWith("+"), "PIX phone must include country code");
            case "PIX_EMAIL" -> require(accountNo.contains("@"), "invalid PIX email");
            case "PIX_CPF" -> require(accountNo.matches("\\d{11}"), "CPF must contain 11 digits");
            case "PIX_CNPJ" -> require(accountNo.matches("\\d{14}"), "CNPJ must contain 14 digits");
            case "PIX_EVP" -> { }
            case "PIX_BANK" -> {
                require(bankCode != null && !bankCode.isBlank(), "PIX bank code is required");
                require(branchBankNo != null && !branchBankNo.isBlank(), "PIX branch number is required");
            }
            default -> throw new IllegalArgumentException("unsupported PIX account type: " + accountType);
        }
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
