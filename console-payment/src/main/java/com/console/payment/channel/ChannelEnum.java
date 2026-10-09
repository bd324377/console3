package com.console.payment.channel;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum ChannelEnum {
    TEST(0,"testPay"),
    U2C(1,"u2cPay"),
    FOUR_Z(2,"fourZPay"),
    CASH(3,"cashPay"),
    CE(4,"cePay");

    @JsonValue
    @EnumValue
    private final Integer key;
    private final String value;

    ChannelEnum(int key, String value) {
        this.key   = key;
        this.value = value;
    }

    public static ChannelEnum getChannelByValue(String value) {
        for (ChannelEnum channel : ChannelEnum.values()) {
            if (Objects.equals(channel.value, value)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
