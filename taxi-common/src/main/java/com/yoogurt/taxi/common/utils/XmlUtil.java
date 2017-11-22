package com.yoogurt.taxi.common.utils;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class XmlUtil {

    /**
     * 创建 {@link Document} 对象
     *
     * @param text XML字符串
     * @return Document
     * @throws DocumentException XML解析异常
     * @author liuwh
     */
    public static Document createDocument(String text) throws DocumentException {
        if (!StringUtils.isBlank(text)) {
            return DocumentHelper.parseText(text);
        }
        return DocumentHelper.createDocument();
    }

    /**
     * 创建XML节点
     *
     * @param name 节点名称，必填
     * @param text 节点内容，选填
     * @return Element
     * @author liuwh
     */
    public static Element createElement(String name, String text) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        Element element = DocumentHelper.createElement(name);
        if (!StringUtils.isBlank(text)) {
            element.setText(text);
        }
        return element;
    }

    /**
     * 递归获取XML中的节点及其值，将其转换成JSON对象
     *
     * @param jsonObject JSON对象
     * @param root       XML根元素
     * @return JSONObject
     * @author liuwh
     */
    public static JSONObject toJSON(JSONObject jsonObject, Element root) {
        List<Element> elementList = root.elements();
        for (Element element : elementList) {
            if (!StringUtils.isBlank(element.getTextTrim())) {
                jsonObject.put(element.getName(), element.getTextTrim());
            }
        }
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            toJSON(jsonObject, e);
        }
        return jsonObject;
    }

    /**
     * 递归获取XML中的节点及其值，将其转换成map对象
     * @param map map对象
     * @param root XML根元素
     * @return Map
     * @author liuwh
     */
    public static Map<String, Object> toMap(Map<String, Object> map, Element root) {
        List<Element> elementList = root.elements();
        for (Element element : elementList) {
            if (!StringUtils.isBlank(element.getTextTrim())) {
                map.put(element.getName(), element.getTextTrim());
            }
        }
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            toMap(map, e);
        }
        return map;
    }
}
