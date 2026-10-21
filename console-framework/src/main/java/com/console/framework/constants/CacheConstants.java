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
    public static final String PAYMENT_CHANNEL = "PAYMENT_CHANNEL";
    public static final String PAYMENT_CHANNEL_MERCHANT_ID = "PAYMENT_CHANNEL:MERCHANT_ID";
    public static final String CASH_OUT_RULE = "CASH_OUT_RULE";//提现规则
    public static final String PRODUCT_PLATFORM = "PRODUCT_PLATFORM";//产品信息
    public static final String PRODUCT = "PRODUCT";//产品信息
    public static final String PLATFORM_RULE_ITEM = "PLATFORM_RULE_ITEM";//产商规则
    public static final String ORDER_MODE = "ORDER_MODE";
    public static final String SYS_ORDER_MODE = "SYS_ORDER_MODE";

    //=========业务==========
    public static final String CAPTCHA_CODE = "CAPTCHA_CODE:";
    public static final String SAME_REG_IP_COUNT = "SAME_REG_IP_COUNT:";
    public static final String SAME_REG_DEVICE_COUNT = "SAME_REG_DEVICE_COUNT:";
    //=========分布式锁=========
    public static final String ACQUIRE_LOCK_CREATE_PAY_ORDER = "ACQUIRE_LOCK:CREATE_PAY_ORDER:";//创建支付订单
    public static final String ACQUIRE_LOCK_UPDATE_PAYMENT_ORDER_STATE = "ACQUIRE_LOCK:UPDATE_PAYMENT_ORDER_STATE:";//修改支付订单状态
    public static final String ACQUIRE_LOCK_HANDLE_PAYMENT_ORDER_STATE = "ACQUIRE_LOCK:HANDLE_PAYMENT_ORDER_STATE:";//修改支付订单状态
    public static final String ACQUIRE_LOCK_PAYMENT_ORDER_CALL_BACK = "ACQUIRE_LOCK:PAYMENT_ORDER_CALL_BACK:";//支付订单回调处理
    public static final String ACQUIRE_LOCK_CREATE_CASH_OUT_ORDER = "ACQUIRE_LOCK:CREATE_CASH_OUT_ORDER:";//创建提现订单
    public static final String ACQUIRE_LOCK_CASH_OUT_ORDER_CALL_BACK = "ACQUIRE_LOCK:CASH_OUT_ORDER_CALL_BACK:";//提现订单回调处理
    public static final String ACQUIRE_LOCK_UPDATE_CASH_OUT_ORDER_STATE = "ACQUIRE_LOCK:UPDATE_CASHOUT_ORDER_STATE:";//修改支付订单状态
    public static final String ACQUIRE_LOCK_SYNC_PRODUCT = "ACQUIRE_LOCK:SYNC_PRODUCT";//同步中转平台产品分布式锁前缀
    public static final String ACQUIRE_LOCK_SYNC_PRODUCT_ORDER = "ACQUIRE_LOCK:SYNC_PRODUCT_ORDER";//同步中转平台产品订单分布式锁前缀
    public static final String ACQUIRE_LOCK_PRODUCT_TRANSFER = "ACQUIRE_LOCK:PRODUCT_TRANSFER:";//进出产品中转分布式锁前缀
}
