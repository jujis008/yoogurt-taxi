package com.yoogurt.taxi.gateway.shiro.cache;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义授权缓存管理类
 */
public class RedisCacheManager extends AbstractCacheManager {

    @Autowired
    private RedisCache<byte[], Object> redisCache;

    @Override
    protected Cache<byte[], Object> createCache(String name) throws CacheException {
        return this.redisCache;
    }
}
