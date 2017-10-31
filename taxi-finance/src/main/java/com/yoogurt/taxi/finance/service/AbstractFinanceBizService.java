package com.yoogurt.taxi.finance.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.*;

@Slf4j
public abstract class AbstractFinanceBizService implements PayChannelService {

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params       需要排序并参与字符拼接的参数组
     * @param parameterMap 字段对应的请求参数，传入null，或者字段名对应的value为null，则以字段名为准
     * @param skipAttrs    从params中跳过的属性
     * @return 拼接后字符串
     */
    public String parameterAssemble(SortedMap<String, Object> params, Map<String, Object> parameterMap, String... skipAttrs) {

        List<String> skipList = new ArrayList<>();
        if (skipAttrs != null && skipAttrs.length > 0) {
            skipList.addAll(Arrays.asList(skipAttrs));
        }
        int i = 0;
        StringBuilder str = new StringBuilder();
        Set<Map.Entry<String, Object>> entrySet = params.entrySet();//所有参与传参的参数按照accsii排序（升序）
        for (Map.Entry<String, Object> entry : entrySet) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null && !StringUtils.isBlank(value.toString()) && !skipList.contains(value.toString())) {
                if (parameterMap != null && parameterMap.get(key) != null) {
                    str.append(parameterMap.get(key));
                } else {
                    str.append(key.trim());
                }
                str.append("=").append(value);
            }
            //最后一个参数不要用&符号
            if (i != entrySet.size() - 1) {
                str.append("&");
            }
            i++;
        }
        log.info(str.toString());
        return str.toString();
    }

    /**
     * 对请求字符串的所有一级value（biz_content作为一个value）进行encode
     *
     * @param params  参数拼接的字符串，&分隔
     * @param charset 编码方式
     * @return encode之后的字符串
     */
    public String parameterEncode(String params, String charset) {
        if (StringUtils.isBlank(params) || !params.contains("&")) return params;
        if (StringUtils.isBlank(charset)) charset = getCharset();
        StringBuilder builder = new StringBuilder();
        String[] paramsArr = params.split("&");
        try {
            for (int i = 0; i < paramsArr.length; i++) {
                String[] val = new String[2];
                val[0] = paramsArr[i].substring(0, paramsArr[i].indexOf("="));
                val[1] = paramsArr[i].substring(paramsArr[i].indexOf("=") + 1, paramsArr[i].length());
                builder.append(val[0]).append("=").append(URLEncoder.encode(val[1], charset));
                if (i != paramsArr.length - 1) {//拼接时，不包括最后一个&字符
                    builder.append("&");
                }
            }
        } catch (Exception e) {
            log.error("参数编码发生异常, {}", e);
            return params;
        }
        return builder.toString();
    }

    public String getNotifyUrl() {
        return "http://m.yoogate.cn/common/finance/i/notify";
    }


    public String getCharset() {
        return "UTF-8";
    }
}
