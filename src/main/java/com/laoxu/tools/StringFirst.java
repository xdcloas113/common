package com.laoxu.tools;

/**
 * 首字母大写
 */
public class StringFirst {
    public static String firstCharUp (String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}