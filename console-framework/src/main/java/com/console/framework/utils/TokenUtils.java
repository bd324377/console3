package com.console.framework.utils;

import com.console.framework.constants.CacheConstants;
import com.console.framework.domain.LoginResult;
import com.console.framework.request.RequestBackendUser;
import com.console.framework.request.RequestMember;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtils {
    private static final String USER_ACCESS_TOKEN_PREFIX = "USER_ACCESS_TOKEN:";
    private static final String USER_REFRESH_TOKEN_PREFIX = "USER_REFRESH_TOKEN:";
    private static final Integer ACCESS_TOKEN_EXPIRE = CacheConstants.MINUTE * 180;
    private static final Integer REFRESH_TOKEN_EXPIRE = CacheConstants.DAY * 7;

    private static final String BACKEND_USER_ACCESS_TOKEN_PREFIX = "BACKEND_USER_ACCESS_TOKEN:";
    private static final String BACKEND_USER_REFRESH_TOKEN_PREFIX = "BACKEND_USER_REFRESH_TOKEN:";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static String generateRandomString() {
        byte[] bytes = new byte[32]; // 256 bits
        SECURE_RANDOM.nextBytes(bytes);
        return ENCODER.encodeToString(bytes);
    }
    public static <T> T getRequestUser(String tokenKey, Class<T> beanClass) {
        return RedisUtils.get(tokenKey, beanClass);
    }

    public static LoginResult createUserToken(RequestMember requestMember) {
        String accessTokenKey = USER_ACCESS_TOKEN_PREFIX + requestMember.getTenantId() + ":" + requestMember.getSiteId() + ":" +
                requestMember.getMarketerId() + ":" + requestMember.getId() + generateRandomString();
        String refreshTokenKey = USER_REFRESH_TOKEN_PREFIX + requestMember.getTenantId() + ":" + requestMember.getSiteId() + ":" +
                requestMember.getMarketerId() + ":" + requestMember.getId() + generateRandomString();
        RedisUtils.setValue(accessTokenKey,requestMember,ACCESS_TOKEN_EXPIRE);
        String refreshTokenData = requestMember.getTenantId() + ";" + requestMember.getId();
        RedisUtils.setValue(refreshTokenKey,refreshTokenData,REFRESH_TOKEN_EXPIRE);
        return LoginResult.builder()
                .token(EncryptUtils.encodeToBase64Str(accessTokenKey))
                .refreshToken(EncryptUtils.encodeToBase64Str(refreshTokenKey))
                .userId(requestMember.getId())
                .build();

    }

    public static LoginResult createBackendUserToken(RequestBackendUser requestBackendUser) {
        String accessTokenKey = BACKEND_USER_ACCESS_TOKEN_PREFIX + requestBackendUser.getBelongTenantId() + ":" + requestBackendUser.getId() + generateRandomString();
        String refreshTokenKey = BACKEND_USER_REFRESH_TOKEN_PREFIX + requestBackendUser.getBelongTenantId() + ":" + requestBackendUser.getId() + generateRandomString();
        RedisUtils.setValue(accessTokenKey,requestBackendUser,ACCESS_TOKEN_EXPIRE);
        String refreshTokenData = requestBackendUser.getTenantId() + ";" + requestBackendUser.getBelongTenantId() + ";" + requestBackendUser.getId();
                RedisUtils.setValue(refreshTokenKey,refreshTokenData,REFRESH_TOKEN_EXPIRE);
        return LoginResult.builder()
                .token(EncryptUtils.encodeToBase64Str(accessTokenKey))
                .refreshToken(EncryptUtils.encodeToBase64Str(refreshTokenKey))
                .userId(requestBackendUser.getId())
                .build();

    }

    public static String getRequestToken(String tokenStr) {
        if (tokenStr == null) return null;
        return EncryptUtils.decodeFromBase64Str(tokenStr);
    }
    public static void deleteUserToken(RequestMember requestMember) {
        String accessTokenKeyPrefix = USER_ACCESS_TOKEN_PREFIX + requestMember.getTenantId() + ":" + requestMember.getSiteId() + ":" +
                requestMember.getMarketerId() + ":" + requestMember.getId() + "*";
        String refreshTokenKeyPrefix = USER_REFRESH_TOKEN_PREFIX + requestMember.getTenantId() + ":" + requestMember.getSiteId() + ":" +
                requestMember.getMarketerId() + ":" + requestMember.getId() + "*";
        RedisUtils.delKeysByPattern(accessTokenKeyPrefix);
        RedisUtils.delKeysByPattern(refreshTokenKeyPrefix);
    }
    public static void deleteBackendUserToken(RequestBackendUser requestBackendUser) {
        String accessTokenKeyPrefix = BACKEND_USER_ACCESS_TOKEN_PREFIX + requestBackendUser.getBelongTenantId() + ":" + requestBackendUser.getId() + "*";
        String refreshTokenKeyPrefix = BACKEND_USER_REFRESH_TOKEN_PREFIX + requestBackendUser.getBelongTenantId() + ":" + requestBackendUser.getId() + "*";
        RedisUtils.delKeysByPattern(accessTokenKeyPrefix);
        RedisUtils.delKeysByPattern(refreshTokenKeyPrefix);
    }
    public static String refreshUserAccessToken(RequestMember requestMember) {
        String accessTokenKeyPrefix = USER_ACCESS_TOKEN_PREFIX + requestMember.getTenantId() + ":" + requestMember.getSiteId() + ":" +
                requestMember.getMarketerId() + ":" + requestMember.getId();
        RedisUtils.delKeysByPattern(accessTokenKeyPrefix + "*");
        String accessTokenKey = accessTokenKeyPrefix + generateRandomString();
        RedisUtils.setValue(accessTokenKey,requestMember,ACCESS_TOKEN_EXPIRE);
        return EncryptUtils.encodeToBase64Str(accessTokenKey);
    }
    public static String refreshBackendUserAccessToken(RequestBackendUser requestBackendUser) {
        String accessTokenKeyPrefix = BACKEND_USER_ACCESS_TOKEN_PREFIX + requestBackendUser.getBelongTenantId() + ":" + requestBackendUser.getId();
        RedisUtils.delKeysByPattern(accessTokenKeyPrefix + "*");
        String accessTokenKey = accessTokenKeyPrefix + generateRandomString();
        RedisUtils.setValue(accessTokenKey,requestBackendUser,ACCESS_TOKEN_EXPIRE);
        return EncryptUtils.encodeToBase64Str(accessTokenKey);
    }
    public static void refreshAccessTokenExpire(String tokenKey) {
        RedisUtils.setExpire(tokenKey, ACCESS_TOKEN_EXPIRE);
    }

    public static String getUserInfoStrByRefreshToken(String refreshToken) {
        String refreshTokenKey = EncryptUtils.decodeFromBase64Str(refreshToken);
        if (!StringUtils.hasText(refreshTokenKey)) return null;
        String userInfoStr = RedisUtils.getStringValue(refreshTokenKey);
        if (StringUtils.hasText(userInfoStr)) {
            RedisUtils.setExpire(refreshTokenKey, REFRESH_TOKEN_EXPIRE);
        }
        return userInfoStr;
    }

    public static void main(String[] args) {
        String encode = EncryptUtils.encodeToBase64Str("1_50238288");
        System.out.println(encode);
        String decode = EncryptUtils.decodeFromBase64Str(encode);
        System.out.println(decode);
    }
}
