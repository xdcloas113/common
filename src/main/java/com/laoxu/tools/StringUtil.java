package com.laoxu.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * String 操作类
 */
public class StringUtil {

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String firstCharUp (String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * string 转换成Intger数组
     *
     * @param str
     * @param splitStr
     * @return
     */
    public final static List<Integer> toIntegerList(String str, String splitStr) {
        if (str != null) {
            ArrayList<Integer> integerList = new ArrayList<Integer>();
            String[] strList = str.split(splitStr);
            for (String string : strList) {
                integerList.add(Integer.parseInt(string));
            }
            return integerList;
        }
        return null;
    }

    /**
     * 功能描述： 反转   右边数字移动到左边，左边的下标往右边 [1,2,5,7,10]  传入数组 , len =2   [7,10,1,2,5]
     * 修改人：xdc
     * 创建时间：2019-05-10 9:48
     * @param a  数组
     * @param len  须要从第几位开始
     */
    public static int[]  reverseArr (int[] a,int len) {
        int[] newArr = new int[a.length];
        int[] houArr = new int[len];
        for (int i =0; i < a.length; i ++) {
            if ( i < a.length-len) {
                newArr[i+len]=a[i];
            } else {
                houArr[a.length-i-1]=a[i];
            }
        }
        a=newArr.clone();
        int[] arr = houArr.clone();
        for(int i=0;i<arr.length ;i++) {
            a[arr.length-i-1] = arr[i];
        }
        return a;
    }

    /**
     * 是否包含特定的值，这个是包含  不是全匹配哈
     * @param array
     * @param value
     * @return
     */
    public static boolean isContains(String[] array, String value) {
        return indexOf(array, value) > -1;
    }

    public static int indexOf(String[] array, String value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].contains(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 生成一个Oracle数据库格式的GUID
     *
     * @return String
     */
    public static String getOracleID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
    /**
     * 生成一个SQLServer数据库格式的GUID
     *
     * @return String
     */
    public static String getSQLServerID() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    public static String getMysqlID() {
        return getOracleID();
    }



}