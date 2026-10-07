package com.console.framework.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;


@Slf4j
public class EncryptUtils {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * 转base64
     * @param string 明文
     */
    public static String encodeToBase64Str(String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        return ENCODER.encodeToString(bytes);
    }

    /**
     * base64转字符串
     * @param string base64文
     */
    public static String decodeFromBase64Str(String string) {
        byte[] bytes = DECODER.decode(string);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * md5加密
     * @param input 明文
     */
    public static String md5Encrypt(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * HmacSHA256 加密
     * @param param     待加密对象
     * @param secretKey 加密秘钥
     * @param salt      加密盐
     * @return 加密结果
     */
    public static String hmacSHA256Encrypt(Object param, String secretKey, String salt) {
        try {
            String str = salt + JSON.toJSONString(param);
            log.debug("加密进行中，加密明文：" + str + ";加密秘钥：" + secretKey);
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            return HexFormat.of().formatHex(mac.doFinal(str.getBytes()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
