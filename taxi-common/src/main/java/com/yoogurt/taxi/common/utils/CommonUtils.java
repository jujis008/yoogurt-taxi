package com.yoogurt.taxi.common.utils;

import org.apache.commons.lang.StringUtils;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.util.CollectionUtils;
import com.yoogurt.taxi.common.constant.Constants;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonUtils {


    private static List<String> PIC_FILE_SUFFIX;

    static {
        PIC_FILE_SUFFIX = new ArrayList<>();
        PIC_FILE_SUFFIX.add(Constants.JPG_FILE_SUFFIX);
        PIC_FILE_SUFFIX.add(Constants.JPEG_FILE_SUFFIX);
        PIC_FILE_SUFFIX.add(Constants.PNG_FILE_SUFFIX);
        PIC_FILE_SUFFIX.add(Constants.ICO_FILE_SUFFIX);
        PIC_FILE_SUFFIX.add(Constants.GIF_FILE_SUFFIX);
    }

    /**
     * 校验图片文件类型
     *
     * @param filename
     * @return
     */
    public static boolean checkPicFile(String filename) {

        return PIC_FILE_SUFFIX.contains(getFileSuffix(filename));

    }

    /**
     * 校验图片文件后缀类型
     *
     * @param suffix
     * @return
     */
    public static boolean checkPicFileSuffix(String suffix) {

        return PIC_FILE_SUFFIX.contains(suffix);

    }

    /**
     * unicode码转汉字
     *
     * @param unicode
     * @return
     */
    public static String convertUnicode(String unicode) {

        if (StringUtils.isBlank(unicode)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        String str = "\\u";
        while ((i = unicode.indexOf(str, pos)) != -1) {
            sb.append(unicode.substring(pos, i));
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }

        return sb.toString();
    }

    /**
     * 获取拼装后的uri
     *
     * @param params
     * @param url
     * @return
     */
    public static String appendUrl(Map<String, String> params, String url) {
        if (CollectionUtils.isEmpty(params) || StringUtils.isBlank(url)) {
            return null;
        }

        StringBuilder builder = new StringBuilder(url).append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        return StringUtils.substring(builder.toString(), 0, builder.length() - 1);
    }

    /**
     * 数组是否存在该字符串
     *
     * @param strs
     * @param parm
     * @return
     */
    public static boolean isExist(String[] strs, String parm) {
        if (strs != null) {
            for (String str : strs) {
                if (StringUtils.equals(str, parm)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 去掉最后一个指定字符
     *
     * @param str
     * @param regex
     * @return
     */
    public static String delLastchar(String str, String regex) {
        if (StringUtils.isNotBlank(str)) {
            String lastChar = StringUtils.substring(str, str.length() - 1, str.length());
            if (StringUtils.equals(regex, lastChar)) {
                return StringUtils.substring(str, 0, str.length() - 1);
            }
        }
        return str;
    }

    /**
     * 校验excel文件类型
     *
     * @param filename
     * @return
     */
    public static boolean checkExcelFile(String filename) {
        String suffix = getFileSuffix(filename);
        if (StringUtils.equalsIgnoreCase(Constants.EXCEL_FILE_SUFFIX_X, suffix)) {
            return true;
        }

        if (StringUtils.equalsIgnoreCase(Constants.EXCEL_FILE_SUFFIX, suffix)) {
            return true;
        }

        return false;
    }

    /**
     * 获取文件后缀名
     *
     * @param filename
     * @return
     */
    public static String getFileSuffix(String filename) {

        if (StringUtils.isBlank(filename)) {
            return "";
        }

        String[] strs = filename.split(Constants.FILE_SUFFIX_SPLIT_MARK);
        if (strs.length < 2) {
            return "";
        }
        return StringUtils.lowerCase(strs[strs.length - 1]);
    }

    /**
     * 取目标字符前两位
     *
     * @param order
     * @return
     */
    public static String beforeTwoLine(String order) {
        if (StringUtils.isBlank(order) || StringUtils.trim(order).length() < 2) {
            return null;
        }

        return StringUtils.substring(StringUtils.trim(order), 0, 2).toUpperCase();
    }

    /**
     * 拼接成三位 001-999 超出不减
     *
     * @param order
     * @return
     */
    public static String concatThreeLine(int order) {

        String str = String.valueOf(order);

        return getThreeLine(str);

    }

    /**
     * 截取后三位或拼接成三位 001-999 超出不减
     *
     * @param str
     * @return
     */
    public static String getThreeLine(String str) {

        if (StringUtils.isBlank(str)) {
            return null;
        }
        str = StringUtils.trim(str);

        StringBuilder builder = new StringBuilder();

        if (str.length() == 1) {
            builder.append("00").append(str);
            return builder.toString();
        }
        if (str.length() == 2) {
            builder.append("0").append(str);
            return builder.toString();
        }
        if (str.length() > 3) {
            return StringUtils.substring(str, str.length() - 3);
        }
        return str;
    }

    /**
     * <p class="detail">
     * 功能：根据文件地址，读取文件内容
     * </p>
     *
     * @param filePath
     * @return
     * @author weihao.liu
     * @date 2016年12月31日
     */
    public static String readFile(String filePath) {
        return readFile(new File(filePath));
    }


    /**
     * <p class="detail">
     * 功能：读取文件内容
     * </p>
     *
     * @param file
     * @return
     * @author weihao.liu
     * @date 2016年12月31日
     */
    public static String readFile(File file) {
        if (file == null || file.isDirectory()) {
            return StringUtils.EMPTY;
        }
        StringBuilder dataBuilder = new StringBuilder();
        FileInputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader bf = null;
        try {
            in = new FileInputStream(file);
            inReader = new InputStreamReader(in, "UTF-8");
            bf = new BufferedReader(inReader);
            String line;
            do {
                line = bf.readLine();
                if (line != null) {
                    if (dataBuilder.length() != 0) {
                        dataBuilder.append("\n");
                    }
                    dataBuilder.append(line);
                }
            } while (line != null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (inReader != null) {
                    inReader.close();
                }
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataBuilder.toString();
    }


    /**
     * 获取真实的ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0
                || Constants.UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || Constants.UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || Constants.UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress)
                    || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        // "***.***.***.***".length() = 15
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * <p class="detail">
     * 功能：获取basepath
     * </p>
     *
     * @param request
     * @return
     * @author weihao.liu
     * @date 2017年08月07日
     */
    public static String getBasePath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    }

    /**
     * <p class="detail">
     * 功能：判断一个字符串的长度，1个汉字=2个字符
     * </p>
     *
     * @param str 需要判断的字符串
     * @return 字符串的实际长度
     * @author liu.weihao
     */
    public static int getLength(String str) {
        if (str == null) {
            return 0;
        }
        char[] c = str.toCharArray();
        int len = 0;
        for (char aC : c) {
            len++;
            if (!isLetter(aC)) {
                len++;
            }
        }
        return len;
    }

    /**
     * <p class="detail">
     * 功能：判断是否是标准的字符
     * </p>
     *
     * @param c 字符
     * @return 是否
     * @author liu.weihao
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0;
    }

    /**
     * 取出姓名前N个字符，后面追加字符。
     *
     * @param name   转换前的名字
     * @param append 转换后名字追加的字符
     * @param left   截取前几个
     * @return 转换后的名字
     */
    public static String convertName(String name, String append, int left) {

        if (StringUtils.isBlank(name)) {
            return StringUtils.EMPTY;
        }
        String leftStr = StringUtils.left(name, left);
        return StringUtils.isBlank(append) ? leftStr : (leftStr + append);
    }

    /**
     * 取出姓名的第一个字符，后面追加字符。
     *
     * @param name   转换前的名字
     * @param append 转换后名字追加的字符
     * @return 转换后的名字
     */
    public static String convertName(String name, String append) {
        return convertName(name, append, 1);
    }

    /**
     * <p class="detail">
     * 功能：XML格式化输出到控制台
     * </p>
     *
     * @param document
     * @author liuwh
     */
    public static void xmlOutput(org.dom4j.Document document) {
        try {
            // 设置输出格式
            // 设置缩进为4个空格，并且另起一行为true
            OutputFormat format = new OutputFormat("    ", true);
            // 输出到控制台
            XMLWriter xmlWriter = new XMLWriter(format);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
