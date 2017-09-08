package com.yoogurt.taxi.gateway.shiro;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    public static final String bearer = HttpServletRequest.BASIC_AUTH.toLowerCase(Locale.ENGLISH);

    @Value("${gateway.jwt.secret}")
    private String secret;

    @Value("${gateway.jwt.header}")
    private String header;

    @Value("${gateway.jwt.expire_seconds}")
    private Integer expirySeconds;

    /**
     * 获取token
     * @param request
     * @return 获取不到，返回 ""，否则返回客户端传来的token
     */
    public String getAuthToken(HttpServletRequest request) {
        if(request == null) return StringUtils.EMPTY;
        String content = request.getHeader(header);
        if(StringUtils.isBlank(content)) return StringUtils.EMPTY;
        if(!StringUtils.startsWith(content, bearer)) return StringUtils.EMPTY;
        String[] contents = content.split(StringUtils.SPACE);
        if(contents.length < 2) return StringUtils.EMPTY;
        return contents[1];
    }

    public String createToken(Map<String, Object> claims) {
        JWTSigner signer = new JWTSigner(secret);
        JWTSigner.Options options = new JWTSigner.Options();
        options.setExpirySeconds(expirySeconds);
        return signer.sign(claims, options);
    }

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
        JWTVerifier verifier = new JWTVerifier(secret);
        return verifier.verify(authToken);
    }
}
