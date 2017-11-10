package com.yoogurt.taxi.finance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.commons.lang.StringUtils;

import java.io.Writer;

/**
 * xml转换工具类
 * @author panwuhai
 * @version $Id: XmlMessageUtil.java, v 0.1 2016年10月8日 下午4:13:49 panwuhai Exp $
 */
public class XmlMessageUtil {

    private static final String  PREFIX_CDATA = "<![CDATA[";
    private static final String  SUFFIX_CDATA = "]]>";

    /**
     * 线程安全单实例
     * 发送xml
     */
    private static final XStream xStream      = new XStream(new XppDriver(new XmlFriendlyReplacer(
                                                  "_-", "_")));

    /** 特殊处理 */
    private static final XStream xcdataStream;

    /**  
     * 初始化XStream  
     * 可支持某一字段可以加入CDATA标签   
     * 如果需要某一字段使用原文  
     * 就需要在String类型的text的头加上"<![CDATA["和结尾处加上"]]>"标签，  
     * 以供XStream输出时进行识别  
     * @return  
     */
    static {
        xcdataStream = new XStream(new XppDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    protected void writeText(QuickWriter writer, String text) {
                        writer.write(PREFIX_CDATA + text + SUFFIX_CDATA);
                    }
                };
            };
        });
    }

    /** 
     * 文本消息对象转换成xml 
     *  
     * @param serialObject 可序列化对象
     * @param format   xml转成数组对象等时头标签的重定义
     * 
     * @return xml 
     */
    public static String serialToXml(Object serialObject, String format) {

        //对指定的类使用 Annotation
        //xstream.processAnnotations(textMessage.getClass());
        // 默认启用 Annotation
        xStream.autodetectAnnotations(true);

        // 头部标签重定义方式
        if (StringUtils.isNotBlank(format)) {
            xStream.alias(format, serialObject.getClass());
        }

        return xStream.toXML(serialObject);

    }

    /**
     * 带CDATA的xml
     * @param serialObject
     * @param format   xml转成数组对象等时头标签的重定义
     * @return
     */
    public static String serialToCDATAXml(Object serialObject, String format) {

        //对指定的类使用 Annotation
        //xstream.processAnnotations(textMessage.getClass());
        // 默认启用 Annotation
        xcdataStream.autodetectAnnotations(true);

        // 头部标签重定义方式
        if (StringUtils.isNotBlank(format)) {
            xcdataStream.alias(format, serialObject.getClass());
        }

        return xcdataStream.toXML(serialObject).replace("__", "_");

    }

    /**
     * 将传入xml字符串转化为java对象
     * @param xmlstr
     * @param cls   xml对应的class类
     * @param format   xml转成数组对象等时头标签的重定义
     * @return T    xml对应的class类的实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseXmlToObject(String xmlstr, Class<T> cls, String format) {
        //注意：不是new Xstream();否则报错：java.lang.NoClassDefFoundError:
        //org/xmlpull/v1/XmlPullParserFactory   
        // 线程不安全解析xml
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        // 数组头部标签重定义方式
        if (StringUtils.isNotBlank(format)) {
            xstream.alias(format, cls);
        }
        xstream.processAnnotations(cls);
        T obj = (T) xstream.fromXML(xmlstr);
        return obj;
    }

}
