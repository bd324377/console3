package com.console.framework.constants;

public class CacheConstants {
    public static final Integer MINUTE = 60;//1分钟
    public static final Integer FIVEMINUTES_SECONDS = 60 * 5;//五分钟
    public static final Integer FIVEMINUTES = 5;//五分钟
    public static final Integer TENMINUTES = 60 * 10;//十分钟
    public static final Integer THIRTYMINUTES = 60 * 30;//十分钟
    public static final Integer HOUR = 1;//1小时
    public static final Integer HOUR_SECONDS = 60 * 60;//1小时
    public static final Integer DAY = 60 * 60 * 24;//一天
    public static final Integer WEEK = 60 * 60 * 24 * 7;//一周
    public static final Integer MONTH  = 60 * 60 * 24 * 30;//一月
    public static final String PARAMETER = "PARAMETER";
    public static final String LOCK_PREFIX = "CONSOLE";
    public static final String TENANT = "TENANT";
    public static final String TENANT_DOMAIN = "TENANT:DOMAIN";//用户前端绑定域名
    public static final String TENANT_BACKEND_DOMAIN = "TENANT:BACKEND:DOMAIN";//管理后台绑定的域名
    public static final String TENANT_LIST = "TENANT_LIST";
    public static final String MARKET_SITE = "MARKET_SITE";
    public static final String MARKET_SITE_LIST = "MARKET_SITE_LIST";
    public static final String MARKET_SITE_DOMAIN_LIST = "MARKET_SITE_DOMAIN_LIST";
    public static final String MARKET_SITE_DOMAIN = "MARKET_SITE_DOMAIN";
    public static final String DEFAULT_MARKET_TEAM = "DEFAULT_MARKET_TEAM";
    public static final String MARKET_TEAM = "MARKET_TEAM";
    public static final String MARKETER = "MARKETER";
    public static final String USER_INVITE_NEW_CONFIG = "USER_INVITE_NEW_CONFIG";//用户邀新配置
    public static final String AGENT = "AGENT";
    public static final String INITIAL_AGENT = "INITIAL_AGENT";
    public static final String VIP = "VIP";
    public static final String INITIAL_VIP = "INITIAL_VIP";

    //=========业务==========
    public static final String CAPTCHA_CODE = "CAPTCHA_CODE:";
    public static final String SAME_REG_IP_COUNT = "SAME_REG_IP_COUNT:";
    public static final String SAME_REG_DEVICE_COUNT = "SAME_REG_DEVICE_COUNT:";
}
