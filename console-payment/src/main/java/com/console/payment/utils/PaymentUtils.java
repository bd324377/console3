package com.console.payment.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** 支付金额转换及渠道签名工具。 */
public final class PaymentUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private PaymentUtils() {
    }

    public static int toCents(BigDecimal amount) {
        // 使用精确运算，禁止静默截断三位小数或超出 Integer 范围的金额。
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("amount must be positive and have at most two decimals");
        }
        return amount.movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).intValueExact();
    }

    public static BigDecimal fromCents(Integer cents) {
        if (cents == null) {
            return null;
        }
        return BigDecimal.valueOf(cents, 2);
    }

    public static Map<String, Object> toMap(Object value) {
        return OBJECT_MAPPER.convertValue(value, new TypeReference<>() { });
    }

    public static String sign(Object value, String secret) {
        return sign(toMap(value), secret);
    }

    public static String sign(Map<String, ?> source, String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("channel secret is blank");
        }
        // U2C 要求剔除 sign、null 和空字符串，再按 key 的 ASCII 自然顺序排序。
        TreeMap<String, Object> ordered = new TreeMap<>();
        source.forEach((key, value) -> {
            if (!"sign".equals(key) && value != null && !String.valueOf(value).isBlank()) {
                ordered.put(key, value);
            }
        });
        // 签名原文：key1=value1&key2=value2&secret=商户密钥。
        String plain = ordered.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&")) + "&secret=" + secret;
        return DigestUtils.md5DigestAsHex(plain.getBytes(StandardCharsets.UTF_8));
    }
}
