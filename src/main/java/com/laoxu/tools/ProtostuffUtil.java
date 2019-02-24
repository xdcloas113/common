package com.laoxu.tools;

import java.io.*;

/**
 *  系列化 和 反序列化
 */
public class ProtostuffUtil {

    //序列化
    public static byte [] serializeble(Object obj){
        byte[] byt= null;
        try {
            ByteArrayOutputStream bai=new ByteArrayOutputStream();
            ObjectOutputStream obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byt=bai.toByteArray();
            bai.close();
            obi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byt;
    }

    //反序列化
    public static Object unserizlize(byte[] byt){
        Object obj = null;
        if (byt == null || byt.length < 1) {
            return null;
        }
        try {
            ByteArrayInputStream bis=new ByteArrayInputStream(byt);
            ObjectInputStream oii=new ObjectInputStream(bis);
            obj=oii.readObject();

            bis.close();
            oii.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}
