package com.yoogurt.taxi.common.helper;

import com.google.common.collect.Maps;
import com.yoogurt.taxi.common.constant.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public final class TokenHelper {

    /**
     * Authorization: basic <access-token>
     */
    private static final String BASIC = "basic";

    /**
     * 头部的键名
     */
    private static final String HEADER = "Authorization";

    /**
     * 生成token的密钥
     */
    private static final String SECRET = "dGF4aSFAIw==";

    /**
     * token过期时间，7天
     */
    private static final int EXPIRE_SECONDS = 604800;

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
        String content = request.getHeader(HEADER);
        if(StringUtils.isBlank(content)) return StringUtils.EMPTY;
        if(!StringUtils.startsWith(content, BASIC)) return StringUtils.EMPTY;
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
    public String createToken(Long userId, String username) {
        Claims claims = new DefaultClaims();
        claims.setId(userId.toString());
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
    public Long getUserId(String token) {
        if(StringUtils.isBlank(token)) return null;
        Long userId;
        try {
            final Claims claims = getClaims(token);
            userId = Long.valueOf(claims.getId());
        } catch (ExpiredJwtException e) {
            userId = null;
            log.error("token过期:{}", e);
        }
        return userId;
    }

    /**
     * 通过request直接获取用户id
     * @param request request
     * @return userId
     */
    public Long getUserId(HttpServletRequest request) {
        String authToken = getAuthToken(request);
        if(StringUtils.isBlank(authToken)) return null;
        return getUserId(authToken);
    }

    /**
     * 获取用户登录名
     * @param token token
     * @return 用户登录名
     */
    public String getUserName(String token) {
        if(StringUtils.isBlank(token)) return null;
        String username;
        try {
            final Claims claims = getClaims(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException e) {
            username = null;
            log.error("token过期:{}", e);
        }
        return username;
    }

    /**
     * 从request中获取username
     * @param request request
     * @return 用户登录名
     */
    public String getUserName(HttpServletRequest request) {
        String authToken = getAuthToken(request);
        if(StringUtils.isBlank(authToken)) return null;
        return getUserName(authToken);
    }

    /**
     * 从请求中获取用户类型
     * @return 用户类型
     */
    public Integer getUserType(HttpServletRequest request) {
        if(request == null) return -1;
        String userType = request.getHeader(Constants.USER_TYPE_HERDER_NAME);
        if(StringUtils.isBlank(userType)) return -1;
        return Integer.valueOf(userType);
    }

    /**
     * 获取token的颁发（创建）时间
     * @param token token
     * @return 颁发（创建）时间
     */
    public Date getCreatedDate(String token) {
        if(StringUtils.isBlank(token)) return null;
        Date created;
        try {
            final Claims claims = getClaims(token);
            created = claims.getIssuedAt();
        } catch (ExpiredJwtException e) {
            created = null;
            log.error("token过期:{}", e);
        }
        return created;
    }

    /**
     * 获取token的过期时间
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDate(String token) {
        if(StringUtils.isBlank(token)) return null;
        Date expiration;
        try {
            final Claims claims = getClaims(token);
            expiration = claims.getExpiration();
        } catch (ExpiredJwtException e) {
            expiration = null;
            log.error("token过期:{}", e);
        }
        return expiration;
    }

    /**
     * 判断token是否已经失效
     * @param token token
     * @return 是否失效
     */
    public Boolean isTokenExpired(String token) {
        if(StringUtils.isBlank(token)) return true;
        final Date expiration = getExpirationDate(token);
        //返回null，可以判定是token过期
        if(expiration == null) return true;
        return expiration.before(new Date());
    }

    /**
     * token剩余的有效时间，单位：毫秒。
     * @param token token
     * @return 剩余时间，过期了，则返回0
     */
    public long remainTimes(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate != null && expirationDate.before(new Date()) ? (expirationDate.getTime() - new Date().getTime()) : 0L;
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
        } catch (ExpiredJwtException e) {
            refreshedToken = null;
            log.error("token过期:{}", e);
        }
        return refreshedToken;
    }

    /**
     * 生成一个token，仅供内部调用
     * @param claims token的负载
     * @return token
     */
    private String generateToken(Claims claims) {
        Map<String, Object> header = Maps.newHashMap();
        header.put("alg", SignatureAlgorithm.HS512.getValue());
        header.put("typ", "JWT");
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 获取token负载信息
     * @param token token
     * @return Claims
     */
    private Claims getClaims(String token) throws ExpiredJwtException {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            claims = null;
            log.error("获取claims异常:{}", e);
        }
        return claims;
    }

    /**
     * 生成过期时间
     * @return 过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRE_SECONDS * 1000);
    }
}
