package com.yoogurt.taxi.gateway.shiro.cache;

import com.yoogurt.taxi.common.helper.RedisHelper;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 自定义RedisCache,将cache对象方法全部实现。
 * 此功能主要用于shiro的缓存，不需要每次都读取数据库。
 */
@Component
public class RedisCache<K, V> implements Cache<K, V> {

    @Autowired
    private RedisHelper redisHelper;

    private String shiroRedisPrefix = "shiro_redis#";

    public RedisCache() {
    }

    @Override
    public V get(K key) throws CacheException {

        if (key == null) return null;
        Object value = redisHelper.getObject(shiroRedisPrefix + key);
        if (value == null) {
            return null;
        }
        return (V) value;
    }

    @Override
    public V put(K key, V value) throws CacheException {

        if (key == null || value == null) return null;
        redisHelper.setObject(shiroRedisPrefix + key, value, 1800);
        return value;
    }

    @Override
    public V remove(K key) throws CacheException {

        if (key == null) return null;

        V value = (V) redisHelper.getObject(shiroRedisPrefix + key);
        redisHelper.del(shiroRedisPrefix + key);
        return value;
    }

    @Override
    public void clear() throws CacheException {
        //TODO 这里要把整个DB都flush掉？
        redisHelper.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public int size() {
        Long len = redisHelper.getConnectionFactory().getConnection().dbSize();
        return len.intValue();
    }

    @Override
    public Set<K> keys() {
        return (Set<K>) redisHelper.keys(shiroRedisPrefix + "*");
    }

    @Override
    public Collection<V> values() {
        Set<K> keys = keys();
        List<V> values = new ArrayList<>(keys.size());
        for (K k : keys) {
            Object o = redisHelper.getObject(shiroRedisPrefix + k);
            if (o != null) {
                values.add((V) o);
            }
        }
        return values;
    }


}
