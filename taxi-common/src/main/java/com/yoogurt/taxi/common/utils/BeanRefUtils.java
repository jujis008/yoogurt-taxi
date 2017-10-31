package com.yoogurt.taxi.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
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
        return toMap(obj);
    }

    public static Map<String, Object> toMap(Object obj) {
        if(obj == null) return null;
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
            log.error("Convert Bean to Map occur exception:{}", e);
        }
        return map;
    }

    /**
     * <p class="detail">
     * 功能：把Bean转换成map对象输出，map的key是按照ASCII码从小到大排序（字典序）
     * </p>
     * @author liuwh
     * @param obj 要转换的bean对象
     * @return SortedMap
     */
    public static SortedMap<String, Object> toSortedMap(Object obj) {
        SortedMap<String, Object> sortedMap = new TreeMap<>();
        Class<?> clz = obj.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (StringUtils.isNoneBlank(methodName) && methodName.startsWith("get") && !methodName.equals("getClass")) {    //继承了Object的getClass方法，要过滤掉
                try {
                    Object value = method.invoke(obj);    //调用Bean的get方法，得到属性值
                    if (value != null) {
                        String key = methodName.substring(3);
                        key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        sortedMap.put(key, value);
                    }
                } catch (Exception e) {
                    log.error("Convert Bean to SortedMap occur exception:{}", e);
                }
            }
        }
        log.info("转换结果: " + sortedMap.toString());
        return sortedMap;
    }

    /**
     * <p class="detail">
     * 功能：根据Bean对象，转成xml格式。
     * 可以指定根元素名称，其他节点名称即为bean的属性名称。
     * </p>
     * @author liuwh
     * @param obj 要转换的bean对象
     * @param rootElementName XML根元素名称，默认“xml”
     * @return Document
     */
    public static Document toXml(Object obj, String rootElementName){
        if (StringUtils.isBlank(rootElementName)) rootElementName = "xml";
        Element rootElement = DocumentHelper.createElement(rootElementName);
        Document document = DocumentHelper.createDocument(rootElement);
        Class<?> clz = obj.getClass();
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (StringUtils.isNoneBlank(methodName) && methodName.startsWith("get") && !methodName.equals("getClass")) {    //继承了Object的getClass方法，要过滤掉
                try {
                    Object value = method.invoke(obj);    //调用Bean的get方法，得到属性值
                    if (value != null) {
                        String key = methodName.substring(3);
                        key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        Element element = DocumentHelper.createElement(key);
                        element.setText(value.toString());
                        rootElement.add(element);
                    }
                } catch (Exception e) {
                    log.error("Convert Bean to XML occur exception:{}", e);
                }
            }
        }
        return document;
    }
}
