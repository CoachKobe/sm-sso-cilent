package com.asiainfo.crm.sm.sso.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.asiainfo.crm.sm.sso.common.SSOContext;

public final class DESPlusHolder {
    //敏感数据
    private static final  DESPlus  DES_PLUS = new DESPlus(SSOContext.DES_KEY);
    //普通数据
    private static final DESPlus  DES_PLUS_E = new DESPlus(SSOContext.DES_KEY_E);
    
    private static Log logger = LogFactoryImpl.getLog(DESPlusHolder.class);

    /**
     * 加密 .
     * @param strIn 
     * @return String
     */
    public static String encrypt(String strIn){
        return DES_PLUS.encrypt(strIn);
    }
    
    /**
     * 解密 .
     * @param strIn 
     * @return String
     */
    public static String decrypt(String strIn){
        return DES_PLUS.decrypt(strIn);
    }
    
    /**
     * 加密 .
     * @param strIn 
     * @return String
     */
    public static String encryptE(String strIn){
        return DES_PLUS_E.encrypt(strIn);
    }
    
    /**
     * 解密
     * @param strIn 
     * @return String
     */
    public static String decryptE(String strIn){
        return DES_PLUS_E.decrypt(strIn);
    }   
    /**
     * 解密
     * @param strIn 
     * @param charSet 
     * @return String
     */
    public static String decryptE(String strIn,String charSet){
        return DES_PLUS_E.decrypt(strIn,charSet);
    }   
    public static void main(String[] args){
        logger.debug(DESPlusHolder.encrypt("no=al1001;pwd=al1001;pty=1;model=1"));
    }

}
