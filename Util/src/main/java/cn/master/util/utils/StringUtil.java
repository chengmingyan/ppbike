package cn.master.util.utils;

import android.util.Base64;

public class StringUtil {
    public static String emptyProcess(Object string) {
        if (string == null) {
            return "暂未提供数据";
        }
        return String.valueOf(string);
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {

            if (!Character.isDigit(str.charAt(i))) {

                return false;
            }

        }

        return true;
    }

    /**
     * if str is not format with base-64 return str
     *
     * @param str
     * @param flags
     * @return
     */
    public static String decode(String str, int flags) {
        String strDecode = null;
        try {
            strDecode = new String(Base64.decode(str, flags));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (strDecode == null) {
                strDecode = str;
            }
        }
        return strDecode;
    }

    /**
     * 字符数组合并
     *
     * @param strings    数组
     * @param expression 特殊字符
     * @return String1
     */
    public static String addString(String[] strings, String expression) {
        StringBuffer string = new StringBuffer();
        for (int i = 0; i < strings.length - 1; i++) {
            string.append(strings[i] + expression);
        }
        string.append(strings[strings.length - 1]);
        return string.toString();
    }

    /**
     * 判断是否为null或空值
     *
     * @param str String
     * @return true or false
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断str1和str2是否相同
     *
     * @param str1 str1
     * @param str2 str2
     * @return true or false
     */
    public static boolean equals(String str1, String str2) {
        return str1 == str2 || str1 != null && str1.equals(str2);
    }

    /**
     * 判断str1和str2是否相同(不区分大小写)
     *
     * @param str1 str1
     * @param str2 str2
     * @return true or false
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * 判断字符串str1是否包含字符串str2
     *
     * @param str1 源字符串
     * @param str2 指定字符串
     * @return true源字符串包含指定字符串，false源字符串不包含指定字符串
     */
    public static boolean contains(String str1, String str2) {
        return str1 != null && str1.contains(str2);
    }

    /**
     * 判断字符串是否为空，为空则返回一个空值，不为空则返回原字符串
     *
     * @param str 待判断字符串
     * @return 判断后的字符串
     */
    public static String getString(String str) {
        return str == null ? "" : str;
    }
}