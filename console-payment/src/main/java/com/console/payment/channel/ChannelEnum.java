package com.console.payment.channel;

import lombok.Getter;

@Getter
public enum ChannelEnum {
    TEST(0,"testPay"),
    U2C(1,"u2cPay"),
    FOUR_Z(2,"four_zPay"),
    CASH(3,"cashPay"),
    CE(4,"cePay");

    private final Integer key;
    private final String value;

    ChannelEnum(int key, String value) {
        this.key   = key;
        this.value = value;
    }
}
