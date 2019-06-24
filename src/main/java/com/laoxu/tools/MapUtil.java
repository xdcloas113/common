package com.laoxu.tools;

import org.apache.commons.beanutils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 所在包：com.scmofit.gifm.common.commonJson
 * 文件名：
 * 版权：四川兴财信息产业发展有限公司.
 * 项目：gifm
 * 功能描述：Map处理空    key value
 * 修改人：xdc
 * 创建时间：2019-02-22 17:41
 */
public class MapUtil {
    /**
     * 移除map中空key或者value空值
     * @param map
     */
    public static void removeNullEntry(Map map){
        removeNullKey(map);
        removeNullValue(map);
    }

    /**
     * 移除map的空key
     * @param map
     * @return
     */
    public static void removeNullKey(Map map){
        Set set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj = (Object) iterator.next();
            remove(obj, iterator);
        }
    }

    /**
     * 移除map中的value空值
     * @param map
     * @return
     */
    public static void removeNullValue(Map map){
        Set set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            Object obj = (Object) iterator.next();
            Object value =(Object)map.get(obj);
            remove(value, iterator);
        }
    }

    /**
     * 移除map中的空值
     *
     */
    private static void remove(Object obj,Iterator iterator){
        if(obj instanceof String){
            String str = (String)obj;
            if(isEmpty(str)){
                iterator.remove();
            }

        }else if(obj instanceof Collection){
            Collection col = (Collection)obj;
            if(col==null||col.isEmpty()){
                iterator.remove();
            }

        }else if(obj instanceof Map){
            Map temp = (Map)obj;
            if(temp==null||temp.isEmpty()){
                iterator.remove();
            }

        }else if(obj instanceof Object[]){
            Object[] array =(Object[])obj;
            if(array==null||array.length<=0){
                iterator.remove();
            }
        }else{
            if(obj==null){
                iterator.remove();
            }
        }
    }

    public static boolean isEmpty(Object obj){
        return obj == null || obj.toString().length() == 0;
    }


    /**
     *  map 转成对象
     * @param clazz 对象的class
     * @param map map
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T>T convertMap(Class<T> clazz, Map map) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz); // 获取类属性
        T obj = clazz.newInstance();
        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                args[0] = value;
                Field privateField = getPrivateField(propertyName, clazz);
                if (privateField == null) {
                }
                privateField.setAccessible(true);
                String type = privateField.getGenericType().toString();
                if (type.equals("class java.lang.String")) {
                    privateField.set(obj, value);
                } else if (type.equals("class java.lang.Boolean")) {
                    privateField.set(obj, Boolean.parseBoolean(String.valueOf(value)));
                } else if (type.equals("class java.lang.Long")) {
                    privateField.set(obj, Long.parseLong(String.valueOf(value)));
                } else if (type.equals("class java.lang.Integer")) {
                    privateField.set(obj, Integer.parseInt(String.valueOf(value)));
                } else if (type.equals("class java.lang.Double")) {
                    privateField.set(obj,Double.parseDouble(String.valueOf(value)));
                } else if (type.equals("class java.lang.Float")) {
                    privateField.set(obj,Float.parseFloat(String.valueOf(value)));
                } else if (type.equals("class java.math.BigDecimal")){
                    privateField.set(obj,new BigDecimal(String.valueOf(value)));
                } else if (type.equals("class java.util.Date")) {
                    if (value.toString().length() == 10) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date utilDate =sdf.parse(String.valueOf(value));
                        privateField.set(obj, utilDate);
                    }else  {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date utilDate =sdf.parse(String.valueOf(value));
                        privateField.set(obj, utilDate);
                    }
                }
                //可继续追加类型
            }
        }
        return obj;
    }
    /*拿到反射父类私有属性*/
    private static Field getPrivateField(String name, Class cls) {
        Field declaredField = null;
        try {
            declaredField = cls.getDeclaredField(name);
        } catch (NoSuchFieldException ex) {

            if (cls.getSuperclass() == null) {
                return declaredField;
            } else {
                declaredField = getPrivateField(name, cls.getSuperclass());
            }
        }
        return declaredField;
    }


    /**
     * @Title:transMap2Bean2
     * @Description:Map转bean
     * @param:[map, obj]
     * @return:void
     * @author: rmk
     */
    public static void transMap2Bean2(Map<String, Object> map, Object obj) throws InvocationTargetException, IllegalAccessException {

        if (map == null || obj == null) {
            return;
        }
        try {
            BeanUtils.populate(obj, map);
        } catch (Exception e) {
            System.out.println("transMap2Bean2 Error " + e);
            throw e;
        }
    }

    /**
     * @Title:transBean2Map
     * @Description:bean转map
     * @param:[obj]
     * @return:java.util.Map<java.lang.String,java.lang.Object>
     * @author: rmk
     */
    public static Map<String, Object> transBean2Map(Object obj) {

        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }



}
