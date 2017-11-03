package com.yoogurt.taxi.order;

import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.order.service.ExpiredMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExpireRedis {
    @Autowired
    private ExpiredMessageListener expiredMessageListener;
    @Autowired
    private RedisHelper redisHelper;

    @Test
    public void testSub() {
        RedisConnection connection = redisHelper.getConnectionFactory().getConnection();
        connection.select(1);
        connection.pSubscribe(expiredMessageListener,"__keyevent@1__:expired".getBytes());
    }

    @Test
    public void testPublish() {
        RedisConnection connection = redisHelper.getConnectionFactory().getConnection();
        connection.select(1);
        connection.setEx("task1".getBytes(),10,"this is test".getBytes());
    }
}
