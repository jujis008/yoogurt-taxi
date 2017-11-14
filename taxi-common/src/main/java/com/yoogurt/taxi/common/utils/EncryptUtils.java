package com.yoogurt.taxi.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class EncryptUtils {

    private static final String UTF8 = "utf-8";

    /**
     * MD5数字签名
     * @param src
     * @return
     * @throws Exception
     */
    public String md5Digest(String src) throws Exception {
        // 定义数字签名方法, 可用：MD5, SHA-1
        byte[] b = DigestUtils.md5(src);
        return this.byte2HexStr(b);
    }

    /**
     * BASE64编码
     * @param src
     * @return
     * @throws Exception
     */
    public String base64Encoder(String src) throws Exception {
        return Base64.encodeBase64String(src.getBytes(UTF8));
    }

    /**
     * 字节数组转化为大写16进制字符串
     * @param b
     * @return
     */
    private String byte2HexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s.toUpperCase());
        }
        return sb.toString();
    }
}
