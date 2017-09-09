package com.yoogurt.taxi.gateway.shiro;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.yoogurt.taxi.gateway.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public final class TokenHelper {

    @Autowired
    private JwtProperties properties;

    /**
     * 获取token
     * @param request
     * @return 获取不到，返回 ""，否则返回客户端传来的token
     */
    public String getAuthToken(HttpServletRequest request) {
        if(request == null) return StringUtils.EMPTY;
        String content = request.getHeader(properties.getHeader());
        if(StringUtils.isBlank(content)) return StringUtils.EMPTY;
        if(!StringUtils.startsWith(content, properties.getBasic())) return StringUtils.EMPTY;
        String[] contents = content.split(StringUtils.SPACE);
        if(contents.length < 2) return StringUtils.EMPTY;
        return contents[1];
    }

    /**
     * 创建一个token
     * @param claims 格外的payload信息
     * @return token
     */
    public String createToken(Map<String, Object> claims) {
        JWTSigner signer = new JWTSigner(properties.getSecret());
        JWTSigner.Options options = new JWTSigner.Options();
        options.setExpirySeconds(properties.getExpireSeconds());
        return signer.sign(claims, options);
    }

    /**
     * 直接获取用户id
     * @param request
     * @return 用户id
     */
    public Object getUserId(HttpServletRequest request) {
        String token = getAuthToken(request);
        if(StringUtils.isBlank(token)) return null;
        try {
            return getClaims(token).get("userId");
        } catch (Exception e) {
            log.error("获取userId异常: {}", e);
        }
        return null;
    }

    public Object getUserId(String authToken) {
        if(StringUtils.isBlank(authToken)) return null;
        try {
            return getClaims(authToken).get("userId");
        } catch (Exception e) {
            log.error("获取userId异常: {}", e);
        }
        return null;
    }

    private Map<String, Object> getClaims(String authToken) throws Exception {
        JWTVerifier verifier = new JWTVerifier(properties.getSecret());
        return verifier.verify(authToken);
    }
}
