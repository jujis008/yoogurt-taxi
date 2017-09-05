package com.yoogurt.taxi.gateway.shiro;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

@Component
public class TokenHelper {

    private static final String bearer = "bearer";

    @Value("${gate.jwt.secret}")
    private String secret;

    @Value("${gate.jwt.header}")
    private String tokenHeaderName;

    @Value("${gate.jwt.expiration}")
    private Integer expirySeconds;

    public String getAuthToken(HttpServletRequest request) {
        if(request == null) return StringUtils.EMPTY;
        String content = request.getHeader(tokenHeaderName);
        if(StringUtils.isBlank(content)) return StringUtils.EMPTY;
        String[] contents = content.split(StringUtils.SPACE);
        if(contents.length < 2) return StringUtils.EMPTY;
        if(!bearer.equals(contents[0])) return StringUtils.EMPTY;
        return contents[1];
    }

    public String createToken(Map<String, Object> claims) {
        JWTSigner signer = new JWTSigner(secret);
        JWTSigner.Options options = new JWTSigner.Options();
        options.setExpirySeconds(expirySeconds);
        return signer.sign(claims, options);
    }

    public Object getUserIdFromToken(HttpServletRequest request) {
        String token = getAuthToken(request);
        if(StringUtils.isBlank(token)) return null;
        try {
            JWTVerifier verifier = new JWTVerifier(secret);
            Map<String, Object> claims = verifier.verify(token);
            return claims.get("userId");
        } catch (NoSuchAlgorithmException | JWTVerifyException | InvalidKeyException | SignatureException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
