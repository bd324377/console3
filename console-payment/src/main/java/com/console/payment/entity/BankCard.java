package com.console.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private String cardCode;
    private Integer userId;
    private String currency;
    private String accountType;
    private String bankCode;
    private String branchBankNo;
    private String branchBankName;
    private String accountNo;
    private String accountName;
    private String accountMobile;
    private String accountEmail;
    private String province;
    private String city;
    private String cpf;
    private String ifsc;
    private Integer status;
    private LocalDateTime createTime;
}
