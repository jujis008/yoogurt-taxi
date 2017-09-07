package com.yoogurt.taxi.common.config;

import com.yoogurt.taxi.common.serializer.FastJsonJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 这是一个坑：http://blog.csdn.net/u014481096/article/details/54134904
 */
@Configuration
public class RedisConfig {

    /**
     * 序列化器
     *
     * @return
     */
    @Bean(name = "fastJsonJsonRedisSerializer")
    public RedisSerializer getFastJsonJsonRedisSerializer() {
        return new FastJsonJsonRedisSerializer<>(Object.class);
    }

    /**
     * 操作模板设置
     * getRedisTemplate
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, String> getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate template = new RedisTemplate();

        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(getFastJsonJsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(getFastJsonJsonRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }

    @Bean(name = "stringRedisTemplate")
    public RedisTemplate<String, String> getStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();
        return template;
    }

}
