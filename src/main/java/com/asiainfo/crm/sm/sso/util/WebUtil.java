package com.asiainfo.crm.sm.sso.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asiainfo.crm.sm.sso.common.SSOContext;

/**
 * 可以设置cookie或获取客户端IP等
 * @author chenf
 * @version 0.1
 * @since JDK1.5
 */
public class WebUtil {
    /**
     * 获取客户端IP地址,该方法在WebUtil里也有,但为了提供给业务系统的认证包比较单一,这里重复写了。
     * @param request 
     * @return String
     */
    public static String getIpAddr(HttpServletRequest request) {
           String ip = request.getHeader("x-forwarded-for");
           if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
               ip = request.getHeader("Proxy-Client-IP");
           }
           if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
               ip = request.getHeader("WL-Proxy-Client-IP");
           }
           if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
               ip = request.getRemoteAddr();
           }
           return ip;
    }
    /**
     * 设置cookie
     * @param response 
     * @param cookieName 
     * @param cookieValue 
     */
    public static void setCookieValue(HttpServletResponse response,String cookieName,String cookieValue){
        Cookie cookie = new Cookie(cookieName,cookieValue);
        //if(SSOContext.DOMAIN!=null && SSOContext.DOMAIN.trim().length()>0){
        //  cookie.setDomain(SSOContext.DOMAIN);
        //}
        cookie.setMaxAge(SSOContext.EXPIRY);
        cookie.setPath(SSOContext.ROOT_PATH);
        response.addCookie(cookie);
    }
    /**
     * 获取cookie
     * @param request 
     * @param cookieName 
     * @return String
     */
    public static String getCookieValue(HttpServletRequest request,String cookieName){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (int i = 0; i < cookies.length; i++) {
                if(cookieName.equals(cookies[i].getName())){
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }
    /**
     * 删除指定名称的cookie 
     * @param request 
     * @param response  
     * @param cookieName 
     * @return
     */
    public static void delCookieValue(HttpServletRequest request,HttpServletResponse response,String cookieName){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (int i = 0; i < cookies.length; i++) {
                if(cookieName.equals(cookies[i].getName())){
                    cookies[i].setValue(null);
                    cookies[i].setMaxAge(0);
                    cookies[i].setPath(SSOContext.ROOT_PATH);
                    response.addCookie(cookies[i]);
                }
            }
        }
    }
    /**
     * 删除指定域下的所有cookie 
     * @param request 
     * @param response 
     * @return
     */
    public static void delCookieValue(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (int i = 0; i < cookies.length; i++) {
                //if(cookieName.equals(cookies[i].getName())){
                cookies[i].setValue(null);
                cookies[i].setMaxAge(0);
                cookies[i].setPath(SSOContext.ROOT_PATH);
                response.addCookie(cookies[i]);
                //}
            }
        }
    }
}
