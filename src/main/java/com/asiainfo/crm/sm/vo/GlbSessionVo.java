package com.asiainfo.crm.sm.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 缓存中保存的登陆信息VO对象.
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @ClassName: GlbSessionVo.java
 *
 * @version: v1.0.0
 * @author:  wangjj9
 * @date: 2017年5月19日 下午4:10:59 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 */
public class GlbSessionVo implements Serializable {
    
   
    private static final long         serialVersionUID = -1220042617383845371L;
    
    /**
     * 员工ID.
     */
    private Long                      staffId;
    
    /**
     * 员工编号.
     */
    private String                    staffCode;
    
    /**
     * 员工类型.
     */
    private String                    staffType;
    
    /**
     * 员工名称.
     */
    private String                    staffName;
    
    /**
     * 员工归属区域ID.
     */
    private Long                      staffRegionId;
    
    /**
     * 员工所属地区编码.
     */
    private String                    staffRegionNbr;
    
    /**
     * 员工所属地区名称.
     */
    private String                    staffRegionName;
    
    /**
     * 系统用户ID.
     */
    private Long                      systemUserId;
    
    /**
     * 系统用户账号.
     */
    private String                    systemUserCode;
    
    /**
     * 员工归属地市ID
     */
    private Long                      staffLanId;
    
    /**
     * 员工归属地市名称.
     */
    private String                    staffLanName;
    
    /**
     * 岗位标识.
     */
    private Long                      sysPostId;
    
    /**
     * 联系电话.
     */
    private String                    contactTele;

    /**
     * 是否超级管理员.
     */
    private boolean                   isSuperManager;
    
    /**
     * 所属渠道列表.
     */
    private List<GlbSessionChannelVo> channels;
    
    /**
     * 有权限的地址列表
     */
    private List<String>        permissions;
    
    /**
     * 当前登录的渠道ID.
     */
    private Long                      curChannelId;
    
    /**
     * 归属省名称.
     */
    private String  proviceName;
    
    /**
     * 归属省ID.
     */
    private Long  proviceId;
    
    /**
     * 归属县市名称.
     */
    private String  countyName  = null;

    /**
     * 归属县市ID.
     */
    private Long  countyId  = null;    
    
    
    /**
     * 客户信息token
     * */
    private String glbSessionId;  
    
    public GlbSessionVo() {
    }
    
   
    public List<String> getPermissions() {
        return permissions;
    }


    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }


    public String getProviceName() {
        return proviceName;
    }

    public void setProviceName(String proviceName) {
        this.proviceName = proviceName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
    
    public String getGlbSessionId() {
        return glbSessionId;
    }


    public void setGlbSessionId(String glbSessionId) {
        this.glbSessionId = glbSessionId;
    }


    public Long getSystemUserId() {
        return systemUserId;
    }
    
    public void setSystemUserId(Long systemUserId) {
        this.systemUserId = systemUserId;
    }
    
    public String getSystemUserCode() {
        return systemUserCode;
    }
    
    public String getStaffRegionNbr() {
        return staffRegionNbr;
    }
    
    public void setStaffRegionNbr(String staffRegionNbr) {
        this.staffRegionNbr = staffRegionNbr;
    }
    
    public void setSystemUserCode(String systemUserCode) {
        this.systemUserCode = systemUserCode;
    }
    
    public Long getStaffId() {
        return staffId;
    }
    
    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
    
    public String getStaffCode() {
        return staffCode;
    }
    
    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }
    
    public String getStaffType() {
        return staffType;
    }
    
    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }
    
    public String getStaffName() {
        return staffName;
    }
    
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    
    public Long getStaffRegionId() {
        return staffRegionId;
    }
    
    public void setStaffRegionId(Long staffRegionId) {
        this.staffRegionId = staffRegionId;
    }
    
    public String getStaffRegionName() {
        return staffRegionName;
    }
    
    public void setStaffRegionName(String staffRegionName) {
        this.staffRegionName = staffRegionName;
    }
    
    public Long getStaffLanId() {
        return staffLanId;
    }
    
    public void setStaffLanId(Long staffLanId) {
        this.staffLanId = staffLanId;
    }
    
    public String getStaffLanName() {
        return staffLanName;
    }
    
    public void setStaffLanName(String staffLanName) {
        this.staffLanName = staffLanName;
    }
    
    public Long getSysPostId() {
        return sysPostId;
    }
    
    public void setSysPostId(Long sysPostId) {
        this.sysPostId = sysPostId;
    }
    
    public List<GlbSessionChannelVo> getChannels() {
        return channels;
    }
    
    public void setChannels(List<GlbSessionChannelVo> channels) {
        this.channels = channels;
    }
    
    public Long getCurChannelId() {
        return curChannelId;
    }
    
    public void setCurChannelId(Long curChannelId) {
        this.curChannelId = curChannelId;
    }
    
    public String getContactTele() {
        return contactTele;
    }
    
    public void setContactTele(String contactTele) {
        this.contactTele = contactTele;
    }
    
    public boolean isSuperManager() {
        return isSuperManager;
    }
    
    public void setSuperManager(boolean isSuperManager) {
        this.isSuperManager = isSuperManager;
    }   
    
    public Long getProviceId() {
        return proviceId;
    }

    public void setProviceId(Long proviceId) {
        this.proviceId = proviceId;
    }

    public Long getCountyId() {
        return countyId;
    }

    public void setCountyId(Long countyId) {
        this.countyId = countyId;
    }
}
