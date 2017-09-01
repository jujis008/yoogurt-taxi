package com.yoogurt.taxi.common.utils;

import org.apache.commons.lang.StringUtils;
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


    public static List<String> PIC_FILE_SUFFIXS;

    static {
        PIC_FILE_SUFFIXS = new ArrayList<>();
        PIC_FILE_SUFFIXS.add(Constants.JPG_FILE_SUFFIX);
        PIC_FILE_SUFFIXS.add(Constants.JPEG_FILE_SUFFIX);
        PIC_FILE_SUFFIXS.add(Constants.PNG_FILE_SUFFIX);
        PIC_FILE_SUFFIXS.add(Constants.ICO_FILE_SUFFIX);
        PIC_FILE_SUFFIXS.add(Constants.GIF_FILE_SUFFIX);
    }

    /**
     * 校验图片文件类型
     * 
     * @param filename
     * @return
     */
    public static boolean checkPicFile(String filename) {

        return PIC_FILE_SUFFIXS.contains(getFileSuffix(filename));

    }

    /**
     * 校验图片文件后缀类型
     * @param suffix
     * @return
     */
    public static boolean checkPicFileSuffix(String suffix) {

        return PIC_FILE_SUFFIXS.contains(suffix);

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

        while ((i = unicode.indexOf("\\u", pos)) != -1) {
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
        if (StringUtils.equalsIgnoreCase(Constants.EXCELX_FILE_SUFFIX, suffix)) {
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
     * @author weihao.liu
     * @date 2016年12月31日 
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
    	return readFile(new File(filePath));
    }

    
    /**
     * <p class="detail">
     * 功能：读取文件内容
     * </p>
     * @author weihao.liu
     * @date 2016年12月31日 
     * @param file
     * @return
     */
    public static String readFile(File file) {
    	if(file == null || file.isDirectory())	return StringUtils.EMPTY;
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
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(in != null)	in.close();
				if(inReader != null)	inReader.close();
				if(bf != null)	bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return dataBuilder.toString();
    }


    /**
     * 获取真实的ip地址
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
			if (ipAddress.equals("127.0.0.1")
					|| ipAddress.equals("0:0:0:0:0:0:0:1")) {
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
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length() = 15
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
	 * @author weihao.liu
	 * @date 2017年08月07日
	 * @param request
	 * @return
	 */
    public static String getBasePath(HttpServletRequest request){
    	return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath() +"/";
    }
    
}
