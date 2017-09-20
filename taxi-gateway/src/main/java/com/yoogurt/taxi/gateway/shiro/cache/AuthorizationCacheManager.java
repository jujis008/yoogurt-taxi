package com.yoogurt.taxi.gateway.shiro.cache;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义授权缓存管理类
 */
public class AuthorizationCacheManager extends AbstractCacheManager {

    @Autowired
    private AuthorizationCache authorizationCache;

    @Override
    protected Cache createCache(String authorizationCacheKey) throws CacheException {
        authorizationCache.setCacheName(authorizationCacheKey);
        return authorizationCache;
    }
}
