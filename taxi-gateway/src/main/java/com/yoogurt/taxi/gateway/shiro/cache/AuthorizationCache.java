package com.yoogurt.taxi.gateway.shiro.cache;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class AuthorizationCache implements Cache<String, SimpleAuthorizationInfo> {

    /**
     * 缓存的名称
     */
    private String cacheName = CacheKey.SHIRO_AUTHORITY_MAP;

    /**
     * 缓存过期时间，默认1小时
     */
    private int expireSeconds = 3600;

    @Autowired
    private RedisHelper redisHelper;

    public AuthorizationCache() {
    }

    public AuthorizationCache(String cacheName, int expireSeconds) {
        this.cacheName = cacheName;
        this.expireSeconds = expireSeconds;
    }

    @Override
    public SimpleAuthorizationInfo get(String key) throws CacheException {
        if(StringUtils.isBlank(key)) return null;
        Object o = redisHelper.getMapValue(cacheName, key);
        if(o != null && o instanceof SimpleAuthorizationInfo) return (SimpleAuthorizationInfo) o;
        return null;
    }

    @Override
    public SimpleAuthorizationInfo put(String key, SimpleAuthorizationInfo simpleAuthorizationInfo) throws CacheException {
        if(StringUtils.isBlank(key) || simpleAuthorizationInfo == null) return null;
        redisHelper.put(cacheName, key, simpleAuthorizationInfo);
        return simpleAuthorizationInfo;
    }

    @Override
    public SimpleAuthorizationInfo remove(String key) throws CacheException {
        if(StringUtils.isBlank(key)) return null;
        Object o = redisHelper.getMapValue(cacheName, key);
        if (o != null && o instanceof SimpleAuthorizationInfo) {
            redisHelper.deleteMap(cacheName, key);
            return (SimpleAuthorizationInfo) o;
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {
        redisHelper.deleteMap(cacheName);
    }

    @Override
    public int size() {
        return redisHelper.getMapSize(cacheName).intValue();
    }

    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public Collection<SimpleAuthorizationInfo> values() {
        return (Collection<SimpleAuthorizationInfo>) redisHelper.mapKeys(cacheName);
    }

    /**
     * 批量删除Map的值
     * @param hashKeys Map对应的key
     */
    public void remove(Object... hashKeys) {
        redisHelper.deleteMap(cacheName, hashKeys);
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
