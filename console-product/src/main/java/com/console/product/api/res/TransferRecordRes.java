package com.console.product.api.res;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferRecordRes implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String     tranId;
    private Integer    tranType;        //1、转入第三方；2、转出第三方
    private BigDecimal amount;
    private BigDecimal beforeAmount;    //前转入金额
}
