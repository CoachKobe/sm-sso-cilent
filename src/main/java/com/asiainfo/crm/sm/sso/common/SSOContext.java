package com.asiainfo.crm.sm.sso.common;

import com.asiainfo.crm.sm.sso.util.PropertiesHolder;

/**
 * 常量配置文件
 */
public class SSOContext {
    
    /**
     * 各业务系统标志
     */
    public static final String BUSI_SYS_FLAG = PropertiesHolder.get("BUSI_SYS_FLAG");
    
    /**
     * 不需要过滤页面
     */
    public static final String ACCREDIT_URLS = PropertiesHolder.get("ACCREDIT_URLS");
    
    /**
     * 不需要过滤页面表达式
     */
    public static final String ACCREDIT_PATTERNS = PropertiesHolder.get("ACCREDIT_PATTERNS");
 
    /**
     * 登出标识
     */
    public static final String LOGOUT_NAME = "logout";
    
    public static final String LOGOUT_VALUE = "now";

    /**
     * 是否开启cookie的认证方式
     */
    public static final boolean OPEN_COOKIE_AUTH = "true".equals(PropertiesHolder.get("OPEN_COOKIE_AUTH"))?true:false;
    
    /**
     * Cookie配置
     */
    public static final int EXPIRY = -1; 
    
    /**
     * Cookie配置
     */
    /**
     * cookie域范围信息
     */
    public static String DOMAIN = PropertiesHolder.get("DOMAIN");
    
    public static final String ROOT_PATH = PropertiesHolder.get("ROOT_PATH");
    
    public static final String BSS_AUTH_TOCKEN = "BSS-AUTH-TOCKEN";
    
    /**
     * 请求返回用户信息的票据
     */
    public static final String REQ_RETURN_USER_TICKET = "tk0";
    
    /**
     * 请求身份认证
     */
    public static final String REQ_AUTH_IDENTITY = "wk0";
    
    /**
     * 请求身份认证的原址
     */
    public static final String REQ_AUTH_IDENTITY_URL = "wu0";
    
    /**
     * 请求判断是否登录标志
     */
    public static final String REQ_CHECK_USER_LOGIN = "au0";
    
    /**
     * 请求返回用户信息的票据
     */
    public static final String REQ_RETURN_USER_LOGIN_SEQID = "ls0";
    
    public static final String SEPARATOR_WORLD_KEY = String.valueOf((char)2);
    
    public static final String SEPARATOR_GROUP_LINE = String.valueOf((char)3);
    
    /**
     * 登录界面url
     */
    public static final String LOGIN_PAGE = PropertiesHolder.get("LOGIN_PAGE");
    
    /**
     * 服务端URL
     */
    public static final String SERVICE_URL = PropertiesHolder.get("SERVICE_URL");
    
    /**
     * 服务端IP
     */
    public static final String SERVICE_IP = PropertiesHolder.get("SERVICE_IP") != null ?
            PropertiesHolder.get("SERVICE_IP") : PropertiesHolder.get("SERVICE_URL");
    
    /**
     * 设置哪些类型URLS需要过滤,如果为*或者为空时过滤全部
     */
    public static final String OPEN_DANGER_SYMBOL_URLS = PropertiesHolder.get("OPEN_DANGER_SYMBOL_URLS");
    
    /**
     * 存放地址匹配模式集合
     */
    public static final String[] OPEN_DANGER_SYMBOL_PATTEN = OPEN_DANGER_SYMBOL_URLS != null ?  OPEN_DANGER_SYMBOL_URLS.split(",") : new String[] {};
    // 特殊符号校验配置 begin--------------------------------------
    
    /**
     * 需要校验的特殊符号
     */
    public static final String DANGER_SYMBOL = PropertiesHolder.get("DANGER_SYMBOL") ;    
    
    /**
     * 是否开启特殊字符校验
     */
    private static String OPEN_DANGER_SYMBOL = PropertiesHolder.get("OPEN_DANGER_SYMBOL");
    
    /**
     * 判断是否开启特殊符合校验
     */
    public static boolean isOpenDangerSymbol() {
        return "true".equals(OPEN_DANGER_SYMBOL);
    }
    
    /**
     * 设置特殊符号校验开关
     */
    public static void setOpenDangerSymbol(String openDangerSymbol) {
        OPEN_DANGER_SYMBOL = openDangerSymbol;
    }
    
    // 特殊符号校验配置 end--------------------------------------
    
    
    // url校验配置 begin--------------------------------------
    /**
     * 是否开启url的认证 
     */
    private static String OPEN_URL_AUTH = PropertiesHolder.get("OPEN_URL_AUTH");
    
    
    /**
     * 判断是否开启Url认证
     */
    public static boolean isOpenUrlAuth() {
        return "true".equals(OPEN_URL_AUTH);
    }
    
    /**
     * 设置Url认证开关
     */
    public static void setOpenUrlAuth(String openUrlAuth) {
        OPEN_URL_AUTH = openUrlAuth;
    }
    // url校验配置 end--------------------------------------
    
    
    // 加密配置 begin--------------------------------------
    /**
     * 自定义密钥
     */
    public static final String DES_KEY_E = "hi asiainfo !!!";
    
    /**
     * 自定义密钥
     */
    public static final String DES_KEY = "Hello AI Key @0987!@#$%+<>{~!";
 
    /**
     * Session中保存用户登陆信息的Key
     */
    public static final String SESSION_KEY_LOGIN_USER = "LOGIN_USER_MODEL";
    
    // 加密配置 end--------------------------------------


}
