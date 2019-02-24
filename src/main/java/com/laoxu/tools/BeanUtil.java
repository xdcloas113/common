package com.laoxu.tools;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *  拷贝属性过滤null值
 */
public class BeanUtil {

    public static Map<String, ?> toMap(Object object) {
        return new HashMap<>(BeanMap.create(object));
    }

    static {
        ConvertUtils.register(new DateConverter(null), java.util.Date.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new ShortConverter(null), Short.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new DoubleConverter(null), Double.class);
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
    }

    static final Map<String, BeanCopier> BEAN_COPIERS = new HashMap<String, BeanCopier>();


    public static byte[] serializeble(Object obj) {
        return ProtostuffUtil.serializeble(obj);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> tClass) {
        Object obj = ProtostuffUtil.unserizlize(bytes);
        return (T) obj;
    }

    public static <T> T deserialize(byte[] bytes) {
        Object obj = ProtostuffUtil.unserizlize(bytes);
        return (T) obj;
    }


    public static void copyProperties(Object srcObj, Object destObj) {
        String key = genKey(srcObj.getClass(), destObj.getClass());
        BeanCopier copier = null;
        if (!BEAN_COPIERS.containsKey(key)) {
            copier = BeanCopier.create(srcObj.getClass(), destObj.getClass(), false);
            BEAN_COPIERS.put(key, copier);
        } else {
            copier = BEAN_COPIERS.get(key);
        }
        copier.copy(srcObj, destObj, null);
    }

    private static String genKey(Class<?> srcClazz, Class<?> destClazz) {
        return srcClazz.getName() + destClazz.getName();
    }

    public static void copyProperties(Map source, Object target) {
        try {
            org.apache.commons.beanutils.BeanUtils.populate(target, source);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    /**
     * 拷贝属性过滤null值
     *
     * @param source
     * @param target
     * @throws BeansException
     */
    public static void copyPropertiesFilterNull(Object source, Object target) throws BeansException {
        org.springframework.util.Assert.notNull(source, "Source must not be null");
        org.springframework.util.Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);
        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() != null) {
                PropertyDescriptor sourcePd = org.springframework.beans.BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object value = readMethod.invoke(source);
                        // 这里判断以下value是否为空 当然这里也能进行一些特殊要求的处理 例如绑定时格式转换等等
                        if (value != null) {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        }
                    } catch (Throwable ex) {
                        throw new FatalBeanException("Could not copy properties from source to target", ex);
                    }
                }
            }
        }
    }


    private static final Map<Class, Map<String, Field>> classFileds = new HashMap<>();

    public static Field getField(String fieldName, Class clasz) {
        if (!classFileds.containsKey(clasz)) {
            Field[] fields = clasz.getDeclaredFields();
            Field.setAccessible(fields, true);
            Map fieldsMap = new HashMap();
            for (Field field : fields) {
                fieldsMap.put(field.getName(), field);
            }
            classFileds.put(clasz, fieldsMap);
        }
        return classFileds.get(clasz).get(fieldName);
    }

    /**
     * @param bean
     * @param fieldName
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Object bean, String fieldName) {
        try {
            Object obj = getField(fieldName, bean.getClass()).get(bean);
            return (T) obj;
        } catch (IllegalAccessException e) {
            throw new ServerException(e);
        }
    }
}