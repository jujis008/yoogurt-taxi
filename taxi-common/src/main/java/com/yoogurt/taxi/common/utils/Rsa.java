package com.yoogurt.taxi.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public class Rsa {

    private static String DEFAULT_CHARSET = "UTF-8";

    private String charset = DEFAULT_CHARSET;

    public static final String RSA_ALGORITHMS = "SHA1withRSA";

    public static final String RSA2_ALGORITHMS = "SHA256withRSA";

    /**
     * RSA签名
     *
     * @param content    待签名数据
     * @param rsaType    加密算法名称
     * @param privateKey 商户私钥
     * @param charset    编码格式
     * @return 签名值
     */
    public static String sign(String content, String rsaType, String privateKey, String charset) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(rsaType) || StringUtils.isBlank(privateKey)) {
            return null;
        }
        if (!RSA_ALGORITHMS.equals(rsaType) && !RSA2_ALGORITHMS.equals(rsaType)) {
            return null;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("Rsa");
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8);
            Signature signature = Signature.getInstance(rsaType);
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            log.error("生成签名发生异常, {}", e);
        }
        return null;
    }

    /**
     * RSA验签名检查
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 公钥
     * @param charset   编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String signType, String publicKey, String charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("Rsa");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(signType);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(charset));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            log.error("验签发生异常, {}", e);
        }
        return false;
    }

    /**
     * 解密
     *
     * @param content    密文
     * @param privateKey 商户私钥
     * @param charset    编码格式
     * @return 解密后的字符串
     */
    public static String decrypt(String content, String privateKey, String charset) throws Exception {
        InputStream ins = null;
        ByteArrayOutputStream writer = null;
        try {
            PrivateKey key = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance("Rsa");
            cipher.init(Cipher.DECRYPT_MODE, key);
            writer = new ByteArrayOutputStream();
            ins = new ByteArrayInputStream(Base64.decodeBase64(content));
            //rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
            byte[] buf = new byte[128];
            int i;
            while ((i = ins.read(buf)) != -1) {
                byte[] block;
                if (buf.length == i) {
                    block = buf;
                } else {
                    block = new byte[i];
                    System.arraycopy(buf, 0, block, 0, i);
                }
                writer.write(cipher.doFinal(block));
            }
            return new String(writer.toByteArray(), charset);
        } catch (Exception e) {
            log.error("签名解密发生异常, {}", e);
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error("关闭流出现异常, {}", e);
            }
        }
        return null;
    }


    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception 密钥解析异常
     */
    private static PrivateKey getPrivateKey(String key) throws Exception {

        byte[] keyBytes;
        KeyFactory keyFactory = KeyFactory.getInstance("Rsa");
        keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(keySpec);
    }

    public static String getDefaultCharset() {
        return DEFAULT_CHARSET;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
