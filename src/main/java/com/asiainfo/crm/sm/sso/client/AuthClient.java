package com.asiainfo.crm.sm.sso.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.springframework.util.PatternMatchUtils;

import com.alibaba.fastjson.JSON;
import com.asiainfo.crm.sm.sso.common.SSOContext;
import com.asiainfo.crm.sm.sso.util.DESPlusHolder;
import com.asiainfo.crm.sm.sso.util.UriStringTokenizer;
import com.asiainfo.crm.sm.sso.util.WebUtil;
import com.asiainfo.crm.sm.vo.GlbSessionVo;

/**
 * 统一认证客户端过滤器
 * 主要职责:
 * 1、判断当前请求是否已经登录过
 * 2、判断用户是否有权限访问当前路径
 * 3、当请求参数中包含认证信息是，通过服务器进行认证
 * 4、支持自定义扩展接口
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @ClassName: AuthClient.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author:  liaomi
 * @date: 2017年11月6日 下午10:29:25 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 */
public class AuthClient implements Filter {
    
    /**
     * 不需要拦截的页面.
     */
    private static Map<String, String> accreditUrls     = new HashMap<String, String>();
    
    /**
     * 不需要拦截的文件后缀
     */
    private static Set<String>         extensions       = new HashSet<String>();
    
    /**
     * 不需要拦截的url表达式
     */
    private static List<String>        accreditPatterns = new ArrayList<String>();
    
    /**
     * 自定义功能扩展类.
     */
    private static ICustHandle         custHandle       = null;

    // 系统标识
    private static String              busiSysFlag    = null;
    
    private static String[]            dangerSymbols    = null;
    
    private static String              charSet          = null;
    
    private static Log                 logger           = LogFactoryImpl.getLog(AuthClient.class);
    
    static{
        // 加载不需要鉴权的文件后缀
        extensions.add("js");
        extensions.add("css");
        extensions.add("png");
        extensions.add("jpg");
        extensions.add("jpeg");
        extensions.add("bmp");
        extensions.add("gif");
        extensions.add("woff");
    }
    
    /**
     * 初始化配置文件
     * @Function: AuthClient::init
     * @Description: 该函数的功能描述
     * @param config
     * @throws ServletException
     * @version: v1.0.0
     * @author: liaomi
     * @date: 2017年11月6日 下午10:29:35 
     *
     * Modification History:
     * Date         Author          Version            Description
     *-------------------------------------------------------------
     */
    public void init(FilterConfig config) throws ServletException {
        logger.debug("AuthClient 初始化....");
        // RuntimeException,判断必须参数是否具备, 如果不具备throw new IllegalStateException        
        
        // 子系统标识，必填
        String busiSys = config.getInitParameter("BusiSys");
        if (busiSys != null) {
            busiSysFlag = busiSys;
        }
        if (busiSysFlag == null && SSOContext.BUSI_SYS_FLAG != null) {
            busiSysFlag = SSOContext.BUSI_SYS_FLAG;
        }
        if (busiSysFlag == null) {
            throw new IllegalStateException("AuthClient初始化失败,缺少子系统标志类型,请在auth-confs.properties配置."
                + "如BUSI_SYS_FLAG=CRM,或在web.xml里配置InitParameter参数为BusiSys.");
        }
        
        // 存储无需鉴权的页面
        if (SSOContext.ACCREDIT_URLS != null && accreditUrls != null) {
            String[] urls = SSOContext.ACCREDIT_URLS.split(",");
            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                if (url != null && url.trim().length() > 0) {
                    if (url.startsWith("/") && url.trim().length() > 0) {
                        url = url.substring(1);
                    }
                    accreditUrls.put(url, "1");
                }
            }
        }
        
        // 存储无需鉴权的页面表达式
        if (SSOContext.ACCREDIT_PATTERNS != null) {
            String[] patterns = SSOContext.ACCREDIT_PATTERNS.split(",");
            for (int i = 0; i < patterns.length; i++) {
                String pattern = patterns[i];
                if (pattern.trim().length() > 0) {
                    accreditPatterns.add(pattern);
                }
            }
        }
        
        // 实例化客户自定义逻辑类
        String custHandleClazz = config.getInitParameter("CustHandleClazz");
        if (custHandleClazz != null && !"".equals(custHandleClazz)) {
            try {
                custHandle = (ICustHandle) Class.forName(custHandleClazz).newInstance();
            } catch (InstantiationException e) {
                logger.error("过滤器参数CustHandleClazz配置的类" + custHandleClazz + ",初始化异常");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                logger.error("过滤器参数CustHandleClazz配置的类" + custHandleClazz + ",初始化安全权限异常");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                logger.error("过滤器参数CustHandleClazz配置的类" + custHandleClazz + ",找不到");
                e.printStackTrace();
            }
        }
        
        // 字符集
        String cs = config.getInitParameter("charSet");
        if (cs != null) {
            charSet = cs;
        }        
        
        // 配置是否开启url鉴权
        String openUrlAuth = config.getInitParameter("openUrlAuth");
        if (openUrlAuth != null) {
            SSOContext.setOpenUrlAuth(openUrlAuth);
        }
        
        // 配置是否开启特殊符号校验
        String ods = config.getInitParameter("OpenDangerSymbol");
        if (ods != null) {
            SSOContext.setOpenDangerSymbol(ods);
        }
        
        // 如果有开启特殊符号安全检查则加载特殊符号
        if (SSOContext.isOpenDangerSymbol()) {
            String dangerSymbol = config.getInitParameter("DangerSymbol");
            if (dangerSymbol == null) {
                dangerSymbol = SSOContext.DANGER_SYMBOL;
            }
            if (dangerSymbol != null) {
                dangerSymbols = dangerSymbol.split(",");
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        accreditUrls.clear();
        accreditUrls = null;
    }
    
    
    private String getPageUrl(HttpServletRequest request, String url) {
        // 构造出当前的请求地址
        String contextPath = request.getContextPath();
        if (contextPath.trim().length() == 0) {
            contextPath = request.getServerName();
            int port = request.getServerPort();
            if (port > 0 && port != 80 || (port == 80 && url.indexOf(":80") != -1)) {
                contextPath = contextPath + ":" + port;
            }
        } else {
            contextPath = contextPath + "/";
        }
        int index = url.lastIndexOf(contextPath);
        String pageUrl = url.substring(index + contextPath.length());
        return pageUrl;
    }
    
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // 是否开启对特殊符号过滤
        if (SSOContext.isOpenDangerSymbol()) {
            if (isFilterUri(request) && checkDangerSymbol(request)) {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().print("{'code':-1,'message':'非法参数传入,不能访问该界面'}");
                return;
            }
        }
        
        // 获取当前的请求地址
        String url = request.getRequestURL().toString();
        logger.debug("当前请求url：" + url);
        
        // 获取具体访问的页面地址
        String pageUrl = getPageUrl(request, url);
        logger.debug("访问的页面地址："+pageUrl);
        
        //获取当前请求的URL包含参数
        String reqPageUrl = getReqPageUrl(request,url);
        logger.debug("访问页面的真实地址请求:"+reqPageUrl);
        /***************** 操作类请求 begin **********************/
        
        // 设置cookie
        if ("staff/login/setLoginCookie".equals(pageUrl)) {
            setLoginCookie(request, response);
            return;
        }
        
        // 登出
        String logoutFlag = request.getParameter(SSOContext.LOGOUT_NAME);
        if (logoutFlag != null || "staff/logout".equals(pageUrl)) {
            clear(request, response);
            return;
        }

        /********************* 操作类请求end ***********************/
        
        /********************** 不用鉴权的情况 begin ***************/
        
        //js,css,图片等资源类文件不校验直接跳过
        int end = url.lastIndexOf(".");
        if(end!=-1){
            String extension = url.substring(end+1);
            if(extensions.contains(extension)){
                filterChain.doFilter(req, resp);
                return;
            }
        }
        
        // 判断是否强制鉴权，如果地址在白名单中，且没有强制鉴权，则进入对应页面
        String enforce = request.getParameter("enforce_auth");
        if (accreditUrls.containsKey(pageUrl)) {
            if (enforce == null || !"y".equals(enforce)) {
                filterChain.doFilter(req, resp);
                return;
            }
        }
        
        // 判断页面范围地址是否满足白名单表达式，如果满足，则进入对应页面
        if (!accreditPatterns.isEmpty()) {
            for (int i = 0; i < accreditPatterns.size(); i++) {
                boolean b = PatternMatchUtils.simpleMatch(accreditPatterns.get(i).toString(),
                    pageUrl);
                if (b) {
                    filterChain.doFilter(req, resp);
                    return;
                }
            }
        }
        
        /********** 不用鉴权的情况 end **********************************/
        
        /********************** 正式鉴权 begin ***************/
        // 身份认证信息
        String wk = request.getParameter(SSOContext.REQ_AUTH_IDENTITY);
        boolean flag = false;
        GlbSessionVo glbSessionVo = (GlbSessionVo) request.getSession().getAttribute(SSOContext.SESSION_KEY_LOGIN_USER);
        System.out.println("鉴权sessionId:"+request.getSession().getId());;
        // 判断是否已经登录
        if (glbSessionVo != null) {
            flag = true;
            logger.debug("已登录过!");
        } else {
            
            // 登陆信息
            String result = null;
            // 如果有开通cookie认证，则先从cookie中获取登陆信息
            boolean isCookieAuth = SSOContext.OPEN_COOKIE_AUTH; 
            if (isCookieAuth) {
                result = WebUtil.getCookieValue(request, SSOContext.BSS_AUTH_TOCKEN);
            }
            
            /**
             * 针对没有wk0的服务请求进行认证，自动加上wk0参数
             * 后续是否考虑都使用这方式去加上wk0
             */
            if(wk == null || "".equals(wk)){
                Cookie[] cookies = request.getCookies();
                if (cookies != null){
                    for (int i = 0; i < cookies.length; i++) {
                        if("ALID".equals(cookies[i].getName())){
                            wk = cookies[i].getValue();
                        }
                    }
                }
            }
            // 如果请求参数中身份认证信息不为空，则先到服务器做身份认证，然后再跳转
            if (result == null && wk != null && wk.trim().length() > 0) {
                //根据配置文件的serviceUrl地址，去获取tk
                String tk = this.getTicket(wk);
                if(null != tk && !"nologin".equals(DESPlusHolder.decryptE(tk, charSet))){
                    StringBuffer info = new StringBuffer();
                    info.append(busiSysFlag).append(SSOContext.SEPARATOR_WORLD_KEY).append(url);
                    info.append(SSOContext.SEPARATOR_WORLD_KEY).append(WebUtil.getIpAddr(request));
                    info.append(SSOContext.SEPARATOR_WORLD_KEY).append(wk);
                    // 请求认证
                    result = getServiceAssert(tk, DESPlusHolder.encryptE(info.toString()));
                    if (isCookieAuth) {
                        WebUtil.setCookieValue(response, SSOContext.BSS_AUTH_TOCKEN, result);
                    }
                }
            }
            // 如果token信息不为空，则解析token内容
            if (null != result) {
                result = DESPlusHolder.decryptE(result, charSet);
                String[] lines = result.split(SSOContext.SEPARATOR_GROUP_LINE);
                if (lines != null && lines.length > 0) {
                    // 根据域分割符对第一行的信息进行分割
                    String[] tmp = lines[0].split(SSOContext.SEPARATOR_WORLD_KEY);
                    // 如果第一个字符为“0”则表示获取数据成功否则表示失败
                    if ("0".equals(tmp[0])) {
                        try{
                            glbSessionVo = JSON.parseObject(lines[1], GlbSessionVo.class);
                            request.getSession().setAttribute(SSOContext.SESSION_KEY_LOGIN_USER, glbSessionVo);
                            // 如果有自定义的类，则在校验成功后，调用自定义服务
                            if (custHandle != null) {
                                custHandle.authHandle(request, response, glbSessionVo);
                            } 
                            flag = true;
                        }catch(Exception ex){
                            logger.error(ex);
                            invalid(response,"-9", "访问失败", SSOContext.LOGIN_PAGE, ex.getMessage());
                            return;
                        }
                    } else {
                        invalid(response,"-9", "访问失败", SSOContext.LOGIN_PAGE, tmp[1]);
                        return;
                    }
                }
            }
        }
        // 如果为true说明已经登录了,则做进一步鉴权
        if (flag) {
            // 判断是否有访问页面的权限
            if (SSOContext.isOpenUrlAuth()) {
                List<String> permissionUrls = glbSessionVo.getPermissions();
                boolean isSuperManager = glbSessionVo.isSuperManager();
                if(!isSuperManager&& permissionUrls != null && !permissionUrls.isEmpty()){
                    boolean hasPermission = false;
                    String strPath = url.substring(url.indexOf(request.getContextPath()));
                    for(String permissionUrl : permissionUrls){
                        if(permissionUrl.trim().length()==0){
                            continue;
                        }
                        if(permissionUrl.contains(strPath)){
                            logger.debug("----------------满足条件，配置地址："+permissionUrl+"; 当前strPath:"+strPath+";----------------");
                            hasPermission = true;
                            break;
                        }
                    }
                    if (!hasPermission) {                        
                        invalid(response,"-9", "访问失败", SSOContext.LOGIN_PAGE, "您没有访问该界面的权限！");
                        return;
                    }            
                }         
            }
            if(wk != null){
                response.sendRedirect(reqPageUrl);
            } else {
                filterChain.doFilter(req, resp);                
            }
        } else {
            // 如果没有登录,直接跳到登陆界面    
            invalid(response,"-9", "访问失败", SSOContext.LOGIN_PAGE, "未检测到登陆信息。");
           /* if (custHandle != null) {
                try {
                    custHandle.noAuthHandle(request, response, filterChain);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                // 跳转到登录界面
                invalid(response,"-9", "访问失败", SSOContext.LOGIN_PAGE, "未检测到登陆信息。");
            }*/
        }
    }
    
    private String getReqPageUrl(HttpServletRequest request, String url) {
        String queryurl=request.getQueryString();  
        if(null!=queryurl){  
            url+="?"+queryurl;  
        }  
        return url;
    }

    /**
     * 检查特殊字符串
     * @Function: AuthClient::checkDangerSymbol
     * @Description: 该函数的功能描述
     * @param request
     * @return
     * @version: v1.0.0
     * @author: liaomi
     * @date: 2017年11月6日 下午10:30:06 
     *
     * Modification History:
     * Date         Author          Version            Description
     *-------------------------------------------------------------
     */
    private boolean checkDangerSymbol(HttpServletRequest request) {
        Enumeration<?> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement().toString();
            String value = request.getParameter(name);
            for (int i = 0; i < dangerSymbols.length; i++) {
                if (value.indexOf(dangerSymbols[i]) != -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 是否是要过滤的地址
     * 
     * @param request
     * @return
     */
    private boolean isFilterUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        UriStringTokenizer pathDirs = new UriStringTokenizer(uri, "/");
        for (int i = 0; i < SSOContext.OPEN_DANGER_SYMBOL_PATTEN.length; i++) {
            final String patten = SSOContext.OPEN_DANGER_SYMBOL_PATTEN[i];
            UriStringTokenizer pattDir = new UriStringTokenizer(patten, "/");
            if (pattDir.pattenMatch(pathDirs)) {
                logger.debug("命中地址:" + uri + " 匹配模式:" + patten);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取服务端Tocken值.
     * 
     * @param ticket
     * @param info
     * @return String
     * @throws IOException
     */
    private String getServiceAssert(String ticket, String info) throws IOException {
        HttpClient httpclient = new HttpClient();
        String serviceUrl = SSOContext.SERVICE_IP + "?tk0=" + ticket;
        logger.debug("serviceUrl = " + serviceUrl);
        PostMethod httppost = new PostMethod(serviceUrl);
        httppost.setParameter("info", info);
        BufferedReader buffer = null;
        InputStream in = null;
        try {
            httpclient.executeMethod(httppost);
            String body = httppost.getResponseBodyAsString();
            logger.debug("body=" + body);
            return body;
        } finally {
            if (buffer != null) {
                buffer.close();
            }
            if (in != null) {
                in.close();
            }
            httppost.releaseConnection();
        }
    }
    
    private String getTicket(String wk) throws IOException {
        HttpClient httpclient = new HttpClient();
        String serviceUrl = SSOContext.SERVICE_IP;
        logger.debug("serviceUrl = " + serviceUrl);
        PostMethod httppost = new PostMethod(serviceUrl);
        httppost.setParameter("wk0", wk);
        BufferedReader buffer = null;
        InputStream in = null;
        try {
            httpclient.executeMethod(httppost);
            String body = httppost.getResponseBodyAsString();
            logger.debug("body=" + body);
            return body;
        } finally {
            if (buffer != null) {
                buffer.close();
            }
            if (in != null) {
                in.close();
            }
            httppost.releaseConnection();
        }
    }
    
    /**
     * 当系统退出时调用
     * 
     * @param context
     * @return
     */
    public void clear(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 清除各web系统的会话
        WebUtil.delCookieValue(request, response);
        GlbSessionVo glbSessionVo = (GlbSessionVo) request.getSession().getAttribute(SSOContext.SESSION_KEY_LOGIN_USER);
        System.out.println("退出时sessionId:"+request.getSession().getId());
        if(glbSessionVo != null){
            String glbSessionId = glbSessionVo.getGlbSessionId();
            // 通知服务端清除会话
            HttpClient httpclient = new HttpClient();
            String serviceUrl = SSOContext.SERVICE_IP;
            logger.debug("清除会话： " + glbSessionId);
            PostMethod httppost = new PostMethod(serviceUrl);
            httppost.setParameter(SSOContext.LOGOUT_NAME, glbSessionId);
            try {
                httpclient.executeMethod(httppost);
                String body = httppost.getResponseBodyAsString();
                logger.debug("body=" + body);
            } finally {
                httppost.releaseConnection();
            }
        }
        //删除session中的用户属性信息
  //      request.getSession().removeAttribute(SSOContext.SESSION_KEY_LOGIN_USER);
        //销毁跟用户关联的session信息
        request.getSession().invalidate();   
        response.sendRedirect(SSOContext.LOGIN_PAGE);
    }
    
    
    /**
     * 设置cookie
     * 
     * @param response
     * @param content
     * @throws IOException
     */
    private void setLoginCookie(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        String token = request.getParameter("token");
        WebUtil.setCookieValue(response, "LTE-SSO-TOKEN", token);
        String areaId = request.getParameter("areaId");
        WebUtil.setCookieValue(response, "LTE-SSO-AREAID", areaId);
        response.setContentType("text/html;charset=GB18030");
        PrintWriter out = response.getWriter();
        out.print("{\"resultCode\":0,\"resultMsg\":\"Cookie写入成功\"}");
        out.flush();
        out.close();
    }
    
    public static boolean invalid(HttpServletResponse resp, String code, String msg, String url, String kickmsg) {
        logger.info("--------认证失败，失败原因："+kickmsg);
        try {
            resp.setStatus(500);
            resp.setHeader("Content-type", "text/html;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(warpBError(code, msg, url, kickmsg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String warpBError(String code, String msg, String url, String kickmsg) {
        return String.format("<script type='text/javascript'>alert('%s,原因:%s');window.location.href='%s';</script>", msg,kickmsg, url);
    }
}
