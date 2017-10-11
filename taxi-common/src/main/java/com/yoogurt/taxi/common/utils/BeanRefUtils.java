package com.yoogurt.taxi.common.utils;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanRefUtils {

    /**
     * 将Bean转换成Map对象
     * @param obj Bean对象，不可为空
     * @param superClass 是否包含父类的属性
     * @return Map对象
     */
    public static Map<?, ?> toMap(Object obj, boolean superClass) {
        if(obj == null) return null;
        if(superClass) return new BeanMap(obj);
        Map<String, Object> map = new HashMap<>();
        Class<?> clz = obj.getClass();
        Field[] fields = clz.getDeclaredFields();
        try {
            for (Field field : fields) {
                String property = field.getName();
                String firstLetter = StringUtils.left(property, 1);
                if (StringUtils.isBlank(firstLetter)) continue; // bean property is empty, continue.
                // get the getXxx method.
                Method method = clz.getMethod(property.replaceFirst(firstLetter, "get" + firstLetter.toUpperCase()));
                map.put(property, method.invoke(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
