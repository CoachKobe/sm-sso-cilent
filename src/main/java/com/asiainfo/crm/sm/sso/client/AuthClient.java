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
 * ͳһ��֤�ͻ��˹�����
 * ��Ҫְ��:
 * 1���жϵ�ǰ�����Ƿ��Ѿ���¼��
 * 2���ж��û��Ƿ���Ȩ�޷��ʵ�ǰ·��
 * 3������������а�����֤��Ϣ�ǣ�ͨ��������������֤
 * 4��֧���Զ�����չ�ӿ�
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @ClassName: AuthClient.java
 * @Description: ����Ĺ�������
 *
 * @version: v1.0.0
 * @author:  liaomi
 * @date: 2017��11��6�� ����10:29:25 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 */
public class AuthClient implements Filter {
    
    /**
     * ����Ҫ���ص�ҳ��.
     */
    private static Map<String, String> accreditUrls     = new HashMap<String, String>();
    
    /**
     * ����Ҫ���ص��ļ���׺
     */
    private static Set<String>         extensions       = new HashSet<String>();
    
    /**
     * ����Ҫ���ص�url���ʽ
     */
    private static List<String>        accreditPatterns = new ArrayList<String>();
    
    /**
     * �Զ��幦����չ��.
     */
    private static ICustHandle         custHandle       = null;

    // ϵͳ��ʶ
    private static String              busiSysFlag    = null;
    
    private static String[]            dangerSymbols    = null;
    
    private static String              charSet          = null;
    
    private static Log                 logger           = LogFactoryImpl.getLog(AuthClient.class);
    
    static{
        // ���ز���Ҫ��Ȩ���ļ���׺
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
     * ��ʼ�������ļ�
     * @Function: AuthClient::init
     * @Description: �ú����Ĺ�������
     * @param config
     * @throws ServletException
     * @version: v1.0.0
     * @author: liaomi
     * @date: 2017��11��6�� ����10:29:35 
     *
     * Modification History:
     * Date         Author          Version            Description
     *-------------------------------------------------------------
     */
    public void init(FilterConfig config) throws ServletException {
        logger.debug("AuthClient ��ʼ��....");
        // RuntimeException,�жϱ�������Ƿ�߱�, ������߱�throw new IllegalStateException        
        
        // ��ϵͳ��ʶ������
        String busiSys = config.getInitParameter("BusiSys");
        if (busiSys != null) {
            busiSysFlag = busiSys;
        }
        if (busiSysFlag == null && SSOContext.BUSI_SYS_FLAG != null) {
            busiSysFlag = SSOContext.BUSI_SYS_FLAG;
        }
        if (busiSysFlag == null) {
            throw new IllegalStateException("AuthClient��ʼ��ʧ��,ȱ����ϵͳ��־����,����auth-confs.properties����."
                + "��BUSI_SYS_FLAG=CRM,����web.xml������InitParameter����ΪBusiSys.");
        }
        
        // �洢�����Ȩ��ҳ��
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
        
        // �洢�����Ȩ��ҳ����ʽ
        if (SSOContext.ACCREDIT_PATTERNS != null) {
            String[] patterns = SSOContext.ACCREDIT_PATTERNS.split(",");
            for (int i = 0; i < patterns.length; i++) {
                String pattern = patterns[i];
                if (pattern.trim().length() > 0) {
                    accreditPatterns.add(pattern);
                }
            }
        }
        
        // ʵ�����ͻ��Զ����߼���
        String custHandleClazz = config.getInitParameter("CustHandleClazz");
        if (custHandleClazz != null && !"".equals(custHandleClazz)) {
            try {
                custHandle = (ICustHandle) Class.forName(custHandleClazz).newInstance();
            } catch (InstantiationException e) {
                logger.error("����������CustHandleClazz���õ���" + custHandleClazz + ",��ʼ���쳣");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                logger.error("����������CustHandleClazz���õ���" + custHandleClazz + ",��ʼ����ȫȨ���쳣");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                logger.error("����������CustHandleClazz���õ���" + custHandleClazz + ",�Ҳ���");
                e.printStackTrace();
            }
        }
        
        // �ַ���
        String cs = config.getInitParameter("charSet");
        if (cs != null) {
            charSet = cs;
        }        
        
        // �����Ƿ���url��Ȩ
        String openUrlAuth = config.getInitParameter("openUrlAuth");
        if (openUrlAuth != null) {
            SSOContext.setOpenUrlAuth(openUrlAuth);
        }
        
        // �����Ƿ����������У��
        String ods = config.getInitParameter("OpenDangerSymbol");
        if (ods != null) {
            SSOContext.setOpenDangerSymbol(ods);
        }
        
        // ����п���������Ű�ȫ���������������
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
        // �������ǰ�������ַ
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
        // �Ƿ�����������Ź���
        if (SSOContext.isOpenDangerSymbol()) {
            if (isFilterUri(request) && checkDangerSymbol(request)) {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().print("{'code':-1,'message':'�Ƿ���������,���ܷ��ʸý���'}");
                return;
            }
        }
        
        // ��ȡ��ǰ�������ַ
        String url = request.getRequestURL().toString();
        logger.debug("��ǰ����url��" + url);
        
        // ��ȡ������ʵ�ҳ���ַ
        String pageUrl = getPageUrl(request, url);
        logger.debug("���ʵ�ҳ���ַ��"+pageUrl);
        
        //��ȡ��ǰ�����URL��������
        String reqPageUrl = getReqPageUrl(request,url);
        logger.debug("����ҳ�����ʵ��ַ����:"+reqPageUrl);
        /***************** ���������� begin **********************/
        
        // ����cookie
        if ("staff/login/setLoginCookie".equals(pageUrl)) {
            setLoginCookie(request, response);
            return;
        }
        
        // �ǳ�
        String logoutFlag = request.getParameter(SSOContext.LOGOUT_NAME);
        if (logoutFlag != null || "staff/logout".equals(pageUrl)) {
            clear(request, response);
            return;
        }

        /********************* ����������end ***********************/
        
        /********************** ���ü�Ȩ����� begin ***************/
        
        //js,css,ͼƬ����Դ���ļ���У��ֱ������
        int end = url.lastIndexOf(".");
        if(end!=-1){
            String extension = url.substring(end+1);
            if(extensions.contains(extension)){
                filterChain.doFilter(req, resp);
                return;
            }
        }
        
        // �ж��Ƿ�ǿ�Ƽ�Ȩ�������ַ�ڰ������У���û��ǿ�Ƽ�Ȩ��������Ӧҳ��
        String enforce = request.getParameter("enforce_auth");
        if (accreditUrls.containsKey(pageUrl)) {
            if (enforce == null || !"y".equals(enforce)) {
                filterChain.doFilter(req, resp);
                return;
            }
        }
        
        // �ж�ҳ�淶Χ��ַ�Ƿ�������������ʽ��������㣬������Ӧҳ��
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
        
        /********** ���ü�Ȩ����� end **********************************/
        
        /********************** ��ʽ��Ȩ begin ***************/
        // �����֤��Ϣ
        String wk = request.getParameter(SSOContext.REQ_AUTH_IDENTITY);
        boolean flag = false;
        GlbSessionVo glbSessionVo = (GlbSessionVo) request.getSession().getAttribute(SSOContext.SESSION_KEY_LOGIN_USER);
        System.out.println("��ȨsessionId:"+request.getSession().getId());;
        // �ж��Ƿ��Ѿ���¼
        if (glbSessionVo != null) {
            flag = true;
            logger.debug("�ѵ�¼��!");
        } else {
            
            // ��½��Ϣ
            String result = null;
            // ����п�ͨcookie��֤�����ȴ�cookie�л�ȡ��½��Ϣ
            boolean isCookieAuth = SSOContext.OPEN_COOKIE_AUTH; 
            if (isCookieAuth) {
                result = WebUtil.getCookieValue(request, SSOContext.BSS_AUTH_TOCKEN);
            }
            
            /**
             * ���û��wk0�ķ������������֤���Զ�����wk0����
             * �����Ƿ��Ƕ�ʹ���ⷽʽȥ����wk0
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
            // �����������������֤��Ϣ��Ϊ�գ����ȵ��������������֤��Ȼ������ת
            if (result == null && wk != null && wk.trim().length() > 0) {
                //���������ļ���serviceUrl��ַ��ȥ��ȡtk
                String tk = this.getTicket(wk);
                if(null != tk && !"nologin".equals(DESPlusHolder.decryptE(tk, charSet))){
                    StringBuffer info = new StringBuffer();
                    info.append(busiSysFlag).append(SSOContext.SEPARATOR_WORLD_KEY).append(url);
                    info.append(SSOContext.SEPARATOR_WORLD_KEY).append(WebUtil.getIpAddr(request));
                    info.append(SSOContext.SEPARATOR_WORLD_KEY).append(wk);
                    // ������֤
                    result = getServiceAssert(tk, DESPlusHolder.encryptE(info.toString()));
                    if (isCookieAuth) {
                        WebUtil.setCookieValue(response, SSOContext.BSS_AUTH_TOCKEN, result);
                    }
                }
            }
            // ���token��Ϣ��Ϊ�գ������token����
            if (null != result) {
                result = DESPlusHolder.decryptE(result, charSet);
                String[] lines = result.split(SSOContext.SEPARATOR_GROUP_LINE);
                if (lines != null && lines.length > 0) {
                    // ������ָ���Ե�һ�е���Ϣ���зָ�
                    String[] tmp = lines[0].split(SSOContext.SEPARATOR_WORLD_KEY);
                    // �����һ���ַ�Ϊ��0�����ʾ��ȡ���ݳɹ������ʾʧ��
                    if ("0".equals(tmp[0])) {
                        try{
                            glbSessionVo = JSON.parseObject(lines[1], GlbSessionVo.class);
                            request.getSession().setAttribute(SSOContext.SESSION_KEY_LOGIN_USER, glbSessionVo);
                            // ������Զ�����࣬����У��ɹ��󣬵����Զ������
                            if (custHandle != null) {
                                custHandle.authHandle(request, response, glbSessionVo);
                            } 
                            flag = true;
                        }catch(Exception ex){
                            logger.error(ex);
                            invalid(response,"-9", "����ʧ��", SSOContext.LOGIN_PAGE, ex.getMessage());
                            return;
                        }
                    } else {
                        invalid(response,"-9", "����ʧ��", SSOContext.LOGIN_PAGE, tmp[1]);
                        return;
                    }
                }
            }
        }
        // ���Ϊtrue˵���Ѿ���¼��,������һ����Ȩ
        if (flag) {
            // �ж��Ƿ��з���ҳ���Ȩ��
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
                            logger.debug("----------------�������������õ�ַ��"+permissionUrl+"; ��ǰstrPath:"+strPath+";----------------");
                            hasPermission = true;
                            break;
                        }
                    }
                    if (!hasPermission) {                        
                        invalid(response,"-9", "����ʧ��", SSOContext.LOGIN_PAGE, "��û�з��ʸý����Ȩ�ޣ�");
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
            // ���û�е�¼,ֱ��������½����    
            invalid(response,"-9", "����ʧ��", SSOContext.LOGIN_PAGE, "δ��⵽��½��Ϣ��");
           /* if (custHandle != null) {
                try {
                    custHandle.noAuthHandle(request, response, filterChain);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                // ��ת����¼����
                invalid(response,"-9", "����ʧ��", SSOContext.LOGIN_PAGE, "δ��⵽��½��Ϣ��");
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
     * ��������ַ���
     * @Function: AuthClient::checkDangerSymbol
     * @Description: �ú����Ĺ�������
     * @param request
     * @return
     * @version: v1.0.0
     * @author: liaomi
     * @date: 2017��11��6�� ����10:30:06 
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
     * �Ƿ���Ҫ���˵ĵ�ַ
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
                logger.debug("���е�ַ:" + uri + " ƥ��ģʽ:" + patten);
                return true;
            }
        }
        return false;
    }
    
    /**
     * ��ȡ�����Tockenֵ.
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
     * ��ϵͳ�˳�ʱ����
     * 
     * @param context
     * @return
     */
    public void clear(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // �����webϵͳ�ĻỰ
        WebUtil.delCookieValue(request, response);
        GlbSessionVo glbSessionVo = (GlbSessionVo) request.getSession().getAttribute(SSOContext.SESSION_KEY_LOGIN_USER);
        System.out.println("�˳�ʱsessionId:"+request.getSession().getId());
        if(glbSessionVo != null){
            String glbSessionId = glbSessionVo.getGlbSessionId();
            // ֪ͨ���������Ự
            HttpClient httpclient = new HttpClient();
            String serviceUrl = SSOContext.SERVICE_IP;
            logger.debug("����Ự�� " + glbSessionId);
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
        //ɾ��session�е��û�������Ϣ
  //      request.getSession().removeAttribute(SSOContext.SESSION_KEY_LOGIN_USER);
        //���ٸ��û�������session��Ϣ
        request.getSession().invalidate();   
        response.sendRedirect(SSOContext.LOGIN_PAGE);
    }
    
    
    /**
     * ����cookie
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
        out.print("{\"resultCode\":0,\"resultMsg\":\"Cookieд��ɹ�\"}");
        out.flush();
        out.close();
    }
    
    public static boolean invalid(HttpServletResponse resp, String code, String msg, String url, String kickmsg) {
        logger.info("--------��֤ʧ�ܣ�ʧ��ԭ��"+kickmsg);
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
        return String.format("<script type='text/javascript'>alert('%s,ԭ��:%s');window.location.href='%s';</script>", msg,kickmsg, url);
    }
}
