package com.yoogurt.taxi.gateway.shiro;

import com.google.common.collect.Maps;
import com.yoogurt.taxi.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public final class TokenHelper {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * TOKEN 的颁发者，规定为yoogurt.taxi.gateway
     */
    private static final String CLAIM_KEY_ISS = "yoogurt.taxi.gateway";

    /**
     * 从request header中获取token
     * @param request 客户端请求
     * @return 获取不到，返回 ""，否则返回客户端传来的token
     */
    public String getAuthToken(HttpServletRequest request) {
        if(request == null) return StringUtils.EMPTY;
        String content = request.getHeader(jwtConfig.getHeader());
        if(StringUtils.isBlank(content)) return StringUtils.EMPTY;
        if(!StringUtils.startsWith(content, jwtConfig.getBasic())) return StringUtils.EMPTY;
        String[] contents = content.split(StringUtils.SPACE);
        if(contents.length < 2) return StringUtils.EMPTY;
        return contents[1];
    }

    /**
     * 创建一个新的token
     * @param userId 用户id
     * @param username 用户登录名
     * @return jwt 颁发的token
     */
    public String createToken(String userId, String username) {
        Claims claims = new DefaultClaims();
        claims.setId(userId);
        claims.setSubject(username);
        claims.setIssuer(CLAIM_KEY_ISS);
        claims.setIssuedAt(new Date());
        return generateToken(claims);
    }

    /**
     * 从token中获取userId
     * @param token token
     * @return 用户id
     */
    public String getUserId(String token) {
        String userId;
        try {
            final Claims claims = getClaims(token);
            userId = claims.getId();
        } catch (Exception e) {
            userId = StringUtils.EMPTY;
            log.error("获取用户ID异常: {}", e);
        }
        return userId;
    }

    /**
     * 通过request直接获取用户id
     * @param request request
     * @return userId
     */
    public String getUserId(HttpServletRequest request) {
        String authToken = getAuthToken(request);
        return getUserId(authToken);
    }

    /**
     * 通过shiro获取用户id，App客户端慎用
     * @return 用户id
     */
    public String getUserId() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.getPrincipal() != null) {
            return subject.getPrincipal().toString();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取用户登录名
     * @param token token
     * @return 用户登录名
     */
    public String getUsername(String token) {
        String username;
        try {
            final Claims claims = getClaims(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = StringUtils.EMPTY;
            log.error("获取用户名异常: {}", e);
        }
        return username;
    }

    /**
     * 获取token的颁发（创建）时间
     * @param token token
     * @return 颁发（创建）时间
     */
    public Date getCreatedDate(String token) {
        Date created;
        try {
            final Claims claims = getClaims(token);
            created = claims.getIssuedAt();
        } catch (Exception e) {
            created = null;
            log.error("获取token创建时间异常: {}", e);
        }
        return created;
    }

    /**
     * 获取token的过期时间
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDate(String token) {
        Date expiration;
        try {
            final Claims claims = getClaims(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
            log.error("获取token过期时间异常: {}", e);
        }
        return expiration;
    }

    /**
     * 判断token是否已经失效
     * @param token token
     * @return 是否失效
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDate(token);
        return expiration.before(new Date());
    }

    /**
     * token剩余的有效时间，单位：毫秒。
     * @param token token
     * @return 剩余时间，过期了，则返回0
     */
    public long remainTimes(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate.before(new Date()) ? 0L : (expirationDate.getTime() - new Date().getTime());
    }

    /**
     * 刷新token，传入的token必须是有效的
     * @param token 原来颁发的token
     * @return 新的token
     */
    public String refreshToken(String token) {
        if(isTokenExpired(token)) return null;
        String refreshedToken;
        try {
            final Claims claims = getClaims(token);
            claims.setIssuedAt(new Date()); //重置颁发时间
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
            log.error("刷新token异常: {}", e);
        }
        return refreshedToken;
    }

    /**
     * 生成一个token，仅供内部调用
     * @param claims token的负载
     * @return token
     */
    protected String generateToken(Claims claims) {
        Map<String, Object> header = Maps.newHashMap();
        header.put("alg", SignatureAlgorithm.HS512.getValue());
        header.put("typ", "JWT");
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    /**
     * 获取token负载信息
     * @param token token
     * @return Claims
     */
    private Claims getClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
            log.error("获取JWT Claims异常: {}", e);
        }
        return claims;
    }

    /**
     * 生成过期时间
     * @return 过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + jwtConfig.getExpireSeconds() * 1000);
    }
}
