package com.asiainfo.crm.sm.sso.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

/**
 * uri表达式匹配类
 * 
 * @author shish
 *
 */
public class UriStringTokenizer extends ArrayList<String> {

    /**
     * 
     */
    private static final long serialVersionUID = 6332132929883410370L;
    
    private static Log logger = LogFactoryImpl.getLog(UriStringTokenizer.class);

    /**
     * 初始化split分割的字符串str
     * 
     * @param str
     *            表达式 /** /** /aa*?a/b.jsp
     * @param split
     *            分隔符 “/”
     * 
     */
    public UriStringTokenizer(String str, String split) {
        super();
        if (str == null || "".equals(str)) {
            return;
        }
        List<String> tmp = Arrays.asList(str.split(split));
        int start = 0;
        int end = 0;
        if (tmp == null || tmp.size() == 0) {
            return;
        }
        if (tmp != null && tmp.size() > 0) {
            if ("".equals(tmp.get(0))) {
                start = tmp.size() >= 1 ? 1 : 0;
            }
            if ("".equals(tmp.get(tmp.size() - 1))) {
                end = tmp.size() >= 1 ? tmp.size() - 1 : 0;
            } else {
                end = tmp.size();
            }
        }
        this.addAll(tmp.subList(start, end));
    }

    /**
     * 匹配单个目录 Wildcard Description </br>
     * ? 匹配任何单字符 </br>
     * * 匹配0或者任意数量的字符 </br>
     * 
     * @param patt
     * @param dir
     * @return
     */
    public static boolean pattSingleDir(String patt, String dir) {
        if (patt == null || "".equals(patt) || dir == null || "".equals(dir)) {
            return false;
        }
        if (isStar(patt)) {
            return true;
        }
        
        List<String> ps = strSplit(patt);
        Iterator<String> it = ps.iterator();
        // 表达式的每个字符拿出来比较
        int pos = 0;
        while (it.hasNext()) {
            char c = ((String)it.next()).charAt(0);
            for (; pos < dir.length();) {
                char target = dir.charAt(pos);
                if ('?' == c || c == target) {
                    pos++;
                    break;
                } else if ('*' == c) {
                    // 下一个表达式字符
                    if (it.hasNext()) {
                        c = ((String)it.next()).charAt(0);
                        // 寻找最后一个c出现的位置
                        Stack<Integer> stack = new Stack<Integer>();
                        for (; pos < dir.length(); pos++) {
                            target = dir.charAt(pos);
                            if (target == c) {
                                stack.push(Integer.valueOf(pos));
                            }
                        }
                        // 如果都没有找到匹配的结果就是不匹配
                        if (stack.isEmpty()) {
                            return false;
                        }

                        if (it.hasNext()) {
                            // 表达式有下一个字符，但是字符串已经匹配完
                            c = ((String)it.next()).charAt(0);
                            if (pos <= dir.length()) {
                                return false;
                            } else {
                                pos = Integer.parseInt(stack.pop().toString()) + 1;
                            }
                        } else {
                            // 表达式没有下一个字符串但是字符串没有匹配完
                            if (pos < dir.length()) {
                                return false;
                            }
                        }
                    } else {
                        return true;
                    }
                } else {
                    // 不一样的正常字符
                    return false;
                }
            }
        }
        if (pos == dir.length()) {
            return true;
        }
        return false;
    }

    /**
     * Ant-Style Path Patterns </br>
     * ? 匹配任何单字符 </br>
     * * 匹配0或者任意数量的字符 </br>
     * ** 匹配0或者更多的目录</br>
     * 当前的模式串是否匹配传入的地址
     * 
     * @param pathDirs
     * @return
     */
    public boolean pattenMatch(UriStringTokenizer pathDirs) {
        Iterator<String> pit = pathDirs.iterator();
        List<String> res = new ArrayList<String>();
        for (Iterator<String> it = this.iterator(); it.hasNext();) {
            String reg = (String)it.next();
            if (isDoubleStar(reg)) {
                if (it.hasNext()) {
                    String nextreg = (String)it.next();
                    while (pit.hasNext()) {
                        String current = (String)pit.next();
                        res.add(current);
                        if (!current.equals(nextreg)) {
                            // 跳出uri循环，模式token循环继续
                            continue;
                        }
                    }
                } else {
                    // 双星在最后把剩下的地址都扔进去
                    while (pit.hasNext()) {
                        String current = (String)pit.next();
                        res.add(current);
                    }
                }
            } else {
                if (pit.hasNext()) {
                    String current = (String)pit.next();
                    // if (pattSingleDir(reg, current)) {
                    if (pattSingleDir(reg, current)) {
                        res.add(current);
                    } else {
                        // 跳出模式token循环，继续下一个模式字符串
                        break;
                    }
                }
            }
        }

        return pathDirs.size() == res.size() ? true : false;
    }

    /**
     * 是否是“**”
     * 
     * @param str
     * @return
     */
    public static boolean isDoubleStar(String str) {
        return "**".equals(str);
    }

    /**
     * 是否是“*”
     * 
     * @param str
     * @return
     */
    public static boolean isStar(String str) {
        return "*".equals(str);
    }

    /**
     * 是否是问号
     * 
     * @param str
     * @return
     */
    public static boolean isQMark(String str) {
        return "?".equals(str);
    }
    
    /**
     * 拆分字符串成list对象,每个字符一个元素.修复split("")方法在java1.5和1.6的不一样处理
     * @param str
     * @return
     * @version: v1.0.0
     * @author: wangjj9
     * @date: 2017年8月9日 下午8:25:47 
     *
     * Modification History:
     * Date         Author          Version            Description
     *-------------------------------------------------------------
     */
    public static List<String> strSplit(String str){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < str.length(); i++) {
            list.add(String.valueOf(str.charAt(i)));
        }
        return list;
    }
    
    public static void main(String[] args) {
        logger.debug(pattSingleDir("*x", "abl"));
        UriStringTokenizer pathDirs = new UriStringTokenizer("/aaa/b/index.html", "/");
        UriStringTokenizer pattDir = new UriStringTokenizer("/*/a?*b/**/index.html", "/");
        logger.debug(pattDir.pattenMatch(pathDirs));
    }
}
