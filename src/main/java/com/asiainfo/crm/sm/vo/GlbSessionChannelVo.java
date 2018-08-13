package com.asiainfo.crm.sm.vo;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @ClassName: GlbSessionChannelVo.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: wuzhy
 * @date: 2017年5月8日 下午8:40:13
 *        Modification History:
 *        Date Author Version Description
 *        ---------------------------------------------------------*
 */
public class GlbSessionChannelVo implements Serializable {
    
    /**
     * .
     */
    private static final long serialVersionUID = -4680755939015998618L;
    // 所属机构id
    private Long              orgId;
    // 渠道名称
    private String            orgName;
    // 所属渠道编码
    private String            channelNbr;
    // 渠道名称
    private String            channelName;
    // 渠道管理代码
    private String            channelManageCode;
    // 渠道类型
    private Long              channelClass;
    // 渠道对应的地区ID
    private Long              channelRegionId;
    // 渠道对应的地区名称
    private String            channelRegionName;
    // 地区
    private Long              channelLanId;
    // 地区名称
    private String            channelLanName;
    
    public Long getOrgId() {
        return orgId;
    }
    
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    
    public String getOrgName() {
        return orgName;
    }
    
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    
    public String getChannelNbr() {
        return channelNbr;
    }
    
    public void setChannelNbr(String channelNbr) {
        this.channelNbr = channelNbr;
    }
    
    public String getChannelName() {
        return channelName;
    }
    
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    
    public String getChannelManageCode() {
        return channelManageCode;
    }
    
    public void setChannelManageCode(String channelManageCode) {
        this.channelManageCode = channelManageCode;
    }
    
    public Long getChannelClass() {
        return channelClass;
    }
    
    public void setChannelClass(Long channelClass) {
        this.channelClass = channelClass;
    }
    
    public Long getChannelRegionId() {
        return channelRegionId;
    }
    
    public void setChannelRegionId(Long channelRegionId) {
        this.channelRegionId = channelRegionId;
    }
    
    public String getChannelRegionName() {
        return channelRegionName;
    }
    
    public void setChannelRegionName(String channelRegionName) {
        this.channelRegionName = channelRegionName;
    }
    
    public Long getChannelLanId() {
        return channelLanId;
    }
    
    public void setChannelLanId(Long channelLanId) {
        this.channelLanId = channelLanId;
    }
    
    public String getChannelLanName() {
        return channelLanName;
    }
    
    public void setChannelLanName(String channelLanName) {
        this.channelLanName = channelLanName;
    }
    
}
