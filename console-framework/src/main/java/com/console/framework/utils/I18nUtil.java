package com.console.framework.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@Slf4j
public class I18nUtil {
    private static final  ResourceBundle en_bundle;
    private static final  ResourceBundle zh_bundle;
    private static final  ResourceBundle pt_bundle;

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    static {
        en_bundle = ResourceBundle.getBundle("messages", Locale.US);
        zh_bundle = ResourceBundle.getBundle("messages", Locale.CHINA, new UTF8Control());
        pt_bundle = ResourceBundle.getBundle("messages", PT_BR, new UTF8Control());
    }

    private I18nUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static String getI8nMsg(String key,String lang) {
        ResourceBundle bundle = switch (lang == null ? "" : lang) {
            case "en" -> en_bundle;
            case "zh" -> zh_bundle;
            default   -> pt_bundle;
        };
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // 至少打日志，不要静默返回 null
            log.warn("i18n key not found: key={}, lang={}", key, lang);
            return key;   // 常见做法：返回 key 本身，方便排查
        }
    }

    public static String getI8nMsg(String key) {
        String lang;
        try {
            lang = RequestUtils.getLang();
        } catch (Exception e) {
            lang = "pt";
        }
        return getI8nMsg(key,lang);
    }

    public static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            try (InputStream is = loader.getResourceAsStream(resourceName)) {
                if (is == null) {
                    return null;
                }
                return new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        }
    }
}
