package com.asiainfo.crm.sm.sso.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Properties文件的读取
 * 
 * @author chenf
 * @version 0.1
 * @since JDK1.4
 */
public final class PropertiesHolder {
    private static Map<String, Object> properties = new HashMap<String, Object>();

    static {
        reload();
    }
    
    public static void reload() {
        Properties props = new Properties();
        InputStream in = PropertiesHolder.class.getResourceAsStream("/auth-confs.properties");
        try {
            if (in != null) {
                props.load(in);
                if (props != null && !props.isEmpty()) {
                    Enumeration<?> en = props.propertyNames();
                    while (en.hasMoreElements()) {
                        String key = (String) en.nextElement();
                        String value = props.getProperty(key);
                        properties.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }
    

    /**
     * 获取某个值 
     * 
     * @param key 
     * @return String
     */
    public static String get(String key) {
        return (String) properties.get(key);
    }

    
    /**
     * 将每个字符串,按分割符转换成map对象
     * @param bodyStr 
     * @param delimiter 
     * @return Map
     */
    public static Map<String, Object> buildMapFormString(String bodyStr,String delimiter){
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        if(bodyStr==null) {
            return bodyMap;
        }
        
        String[] bodyStrs = bodyStr.split(delimiter);
        
        for(int i=0;i<bodyStrs.length;i++){
            String bss = bodyStrs[i];
            String[] bsss = bss.split("=");
            bodyMap.put(bsss[0], bsss[1]);
        }
        
        return bodyMap;
    }
}
