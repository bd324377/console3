package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("s_bank_card")
public class BankCard implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private BankCardType type;
    private String bankCardCode;
    private BankCardStatus status;//状态0、待使用；1、首次出款中；2、正常；3、禁用
    private String userName;//用户名
    private LocalDateTime bindTime;//添加时间
    private LocalDateTime activateTime;//激活时间（首次使用）
    private String bankCode; // 通用银行编码
    private String branchBankNo; // 通用支行编码
    private String remarks;//备注

    @Getter
    public enum BankCardType {//银行卡类型，1、银行卡，2、手机号码，3、邮箱地址，4、CNPJ，5、EVP
        UNFIND(0, "未知"),
        CPF(1,"PIX_CPF"),
        PHONE(2, "PIX_PHONE"),
        EMAIL(3,"PIX_EMAIL"),
        CNPJ(4,"PIX_CNPJ"),
        EVP(5,"PIX_EVP");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        BankCardType(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Getter
    public enum BankCardStatus {//状态0、待使用；1、首次出款中；2、正常；3、禁用
        PENDING(0, "待使用"),
        CASHOUTING(1,"首次出款中"),
        ACTIVIVE(2, "正常"),
        DISABLED(3,"禁用");

        @JsonValue
        @EnumValue
        private final Integer key;
        private final String value;

        BankCardStatus(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
