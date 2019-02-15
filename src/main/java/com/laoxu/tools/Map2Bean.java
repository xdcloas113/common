package com.laoxu.tools;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 功能描述： Apache  Map对拷bean对象
 * 创建人 ：xdc
 */
public class Map2Bean {
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
}
