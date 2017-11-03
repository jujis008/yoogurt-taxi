package com.yoogurt.taxi.common.utils;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class XmlUtil {

    /**
     * <p class="detail">
     * 功能：创建 {@link Document} 对象
     * </p>
     *
     * @param text XML字符串
     * @return Document
     * @throws DocumentException XML解析异常
     * @author liuwh
     */
    public static Document createDocument(String text) throws DocumentException {
        if (!StringUtils.isBlank(text)) return DocumentHelper.parseText(text);
        return DocumentHelper.createDocument();
    }

    /**
     * <p class="detail">
     * 功能：创建XML节点
     * </p>
     *
     * @param name 节点名称，必填
     * @param text 节点内容，选填
     * @return Element
     * @author liuwh
     */
    public static Element createElement(String name, String text) {
        if (StringUtils.isBlank(name)) return null;
        Element element = DocumentHelper.createElement(name);
        if (!StringUtils.isBlank(text)) {
            element.setText(text);
        }
        return element;
    }

    /**
     * <p class="detail">
     * 功能：递归获取XML中的节点及其值
     * </p>
     *
     * @param jsonObject JSON对象
     * @param root       XML根元素
     * @return JSONObject
     * @throws JSONException JSON解析异常
     * @author liuwh
     */
    public static JSONObject toJSON(JSONObject jsonObject, Element root) throws JSONException {
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
