package com.yoogurt.taxi.common.helper;

import com.yoogurt.taxi.common.utils.SerializeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * redis 帮助类
 * @author Eric Lau
 * @Date 2017/8/31
 */
@Component
public class RedisHelper {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取缓存内容
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 根据 key 获取对应的 value
     * @param key  键，不可为null
     * @param defaultVal 如果找不到，返回默认值
     * @param setIfNull  如果key不存在，是否创建 {@Code <key, defaultVal> }
     * @param expireSeconds 缓存过期时间，只有get的值不为null，setIfNull=true时，此参数生效
     * @return 获取的value
     */
    public <V> V get(String key, V defaultVal, boolean setIfNull, int expireSeconds) {

        if (StringUtils.isBlank(key)) return null;
        Object value = get(key);
        if (value == null) {
            if(setIfNull) {
                set(key, defaultVal, expireSeconds);
                return defaultVal;
            }
            return null;
        }
        return (V) value;
    }

    /**
     * 根据 key 获取对应的 value
     * @param key  键，不可为null
     * @param defaultVal 如果找不到，返回默认值
     * @param setIfNull  如果key不存在，是否创建 {@Code <key, defaultVal> }
     * @return 获取的value
     */
    public <V> V get(String key, V defaultVal, boolean setIfNull) {

        if (StringUtils.isBlank(key)) return null;
        Object value = get(key);
        if (value == null) {
            if(setIfNull) {
                set(key, defaultVal);
                return defaultVal;
            }
            return null;
        }
        return (V) value;
    }

    /**
     * 设置缓存
     * @param key   键
     * @param value 值
     * @return
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存，并指定超时时间
     * @param key
     * @param value
     * @param expirySeconds
     */
    public void set(String key, Object value, int expirySeconds) {
        redisTemplate.opsForValue().set(key, value, expirySeconds, TimeUnit.SECONDS);
    }

    /**
     * 删除缓存内容
     * @param key 键
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 自增原子操作
     * @param key
     * @param delta
     * @return
     */
    public Long incrBy(String key, long delta){
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 获取Map缓存
     * @param key 键
     * @return Map结构的缓存对象
     */
    public Map<String, String> getMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置Map缓存
     * @param key   键
     * @param value 值
     */
    public void setMap(String key, Map<String, String> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }

    /**
     * 获取缓存（复杂对象）
     * @param key 键
     * @return 通常为自定义类
     * @see #get(String)
     */
    public Object getObject(final String key) {
        Object object = null;
        object = redisTemplate.execute((RedisCallback<Object>) connection -> {

            byte[] keyr = key.getBytes();
            byte[] value = connection.get(keyr);
            if (value == null) {
                return null;
            }
            return SerializeUtils.unserialize(value);
        });
        return object;
    }

    /**
     * 设置缓存（复杂对象）
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间（单位：s），0为不超时
     */
    public void setObject(String key, Object value, long cacheSeconds) {
        final String keyf = key;
        final Object valuef = value;
        final long liveTime = cacheSeconds;

        redisTemplate.execute((RedisCallback<Long>) connection -> {
            byte[] keyb = keyf.getBytes();
            byte[] valueb = SerializeUtils.serialize(valuef);
            connection.set(keyb, valueb);
            if (liveTime > 0) {
                connection.expire(keyb, liveTime);
            }
            return 1L;
        });
    }

}
