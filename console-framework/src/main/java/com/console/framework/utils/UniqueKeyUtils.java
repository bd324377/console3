package com.console.framework.utils;

import org.springframework.dao.DuplicateKeyException;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UniqueKeyUtils {
    private static final Pattern KEY_PATTERN =
            Pattern.compile("for key '([^']+)'");

    public static String extractUniqueKey(DuplicateKeyException e) {
        Throwable root = e.getMostSpecificCause();
        if (root instanceof SQLException sqlEx) {
            // MySQL 唯一键冲突
            if (sqlEx.getErrorCode() == 1062) {
                String message = sqlEx.getMessage();
                Matcher matcher = KEY_PATTERN.matcher(message);
                if (matcher.find()) {
                    String key = matcher.group(1);
                    // MySQL 8.0 可能是 'user.uk_user_account'，取最后一段
                    int idx = key.lastIndexOf('.');
                    return idx >= 0 ? key.substring(idx + 1) : key;
                }
            }
        }
        return null;
    }
}
