package com.console.payment.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentUtilsTest {
    @Test
    void signsUsingSortedNonEmptyFieldsAndExcludesSign() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("orange", "orange");
        fields.put("banana", "yellow");
        fields.put("empty", "");
        fields.put("apple", "red");
        fields.put("sign", "ignored");

        assertEquals("e420120e5e0e76b6eaec317c983b30a5", PaymentUtils.sign(fields, "asecretkey"));
    }

    @Test
    void convertsMoneyExactly() {
        assertEquals(1234, PaymentUtils.toCents(new BigDecimal("12.34")));
        assertEquals(new BigDecimal("12.34"), PaymentUtils.fromCents(1234));
        assertThrows(IllegalArgumentException.class, () -> PaymentUtils.toCents(new BigDecimal("1.001")));
        assertThrows(IllegalArgumentException.class, () -> PaymentUtils.toCents(BigDecimal.ZERO));
        assertThrows(ArithmeticException.class, () -> PaymentUtils.toCents(new BigDecimal("999999999999.99")));
    }
}
