package com.yoogurt.taxi.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Description:
 * 本系统基于 BCrypt 增强哈希函数进行密码加密。
 * 如果需要其他加密算法，可以直接使用 DigestUtils 工具类。
 * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 * @see org.apache.commons.codec.digest.DigestUtils
 * @author Eric Lau
 * @Date 2017/9/6.
 */
public class Encipher {

    /**
     * 加密强度，取值范围：[4, 31]，默认使用10
     * <p>
     * 加密强度切勿过高，否则会拉长加密的时间，容易导致请求TIMEOUT。
     * </p>
     */
    private static final int STRENGTH = 10;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(STRENGTH);

    /**
     * 对内容进行加密
     * @param content 需要加密的内容
     * @return 加密后的内容
     */
    public static String encrypt(String content) {
        return encoder.encode(content);
    }

    /**
     * 匹配原始内容和加密内容
     * @param raw 原始内容
     * @param encryption 加密内容
     * @return 是否匹配
     */
    public static boolean matches(String raw, String encryption) {
        return encoder.matches(raw, encryption);
    }

}
