package com.asiainfo.crm.sm.sso.client;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.asiainfo.crm.sm.vo.GlbSessionVo;

/**
 * 客户端自定义接口类
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @ClassName: ICustHandle.java
 * @Description: 该类的功能描述
 * 
 * @version: v1.0.0
 * @author:  liaomi
 * @date: 2017年11月6日 下午8:46:09 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 */
public interface ICustHandle {

    /**
     * 登陆失败或者认证失败之后，客户端处理逻辑（可优化为自动跳转登陆界面）
     * @Function: ICustHandle::noAuthHandle
     * @Description: 该函数的功能描述
     * @param req
     * @param resp
     * @param filterChain
     * @version: v1.0.0
     * @author: liaomi
     * @throws Exception 
     * @date: 2017年11月6日 下午8:46:51 
     *
     * Modification History:
     * Date         Author          Version            Description
     *-------------------------------------------------------------
     */
    public void noAuthHandle(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws Exception;
    
    /**
     * 登陆成功认证，获取用户信息
     * @Function: ICustHandle::authHandle
     * @Description: 该函数的功能描述
     * @param req
     * @param resp
     * @param glbSessionVo
     * @version: v1.0.0
     * @author: liaomi
     * @date: 2017年11月6日 下午8:47:38 
     *
     * Modification History:
     * Date         Author          Version            Description
     *-------------------------------------------------------------
     */
    public void authHandle(ServletRequest req, ServletResponse resp, GlbSessionVo glbSessionVo);
}
