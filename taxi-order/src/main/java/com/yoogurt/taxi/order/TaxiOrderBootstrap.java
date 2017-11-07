package com.yoogurt.taxi.order;

import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.order.service.ExpiredMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

@Slf4j
@EnableFeignClients
@EnableTransactionManagement
@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
@ComponentScan({"com.yoogurt.taxi"})
public class TaxiOrderBootstrap {

	public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext context = SpringApplication.run(TaxiOrderBootstrap.class, args);
        ConfigurableEnvironment env = context.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n\t" +
                        "----------------------------------------------------------",

                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));

        redisSubscribe(context);
	}

    private static void redisSubscribe(ConfigurableApplicationContext context) {

        ExpiredMessageListener listener = context.getBean(ExpiredMessageListener.class);
        RedisHelper helper = context.getBean(RedisHelper.class);
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        RedisConnectionFactory connectionFactory = helper.getConnectionFactory();
        String pattern = "__keyevent@*__:expired";
        log.info("设置监听器，pattern为"+pattern);
        listenerContainer.addMessageListener(listener, new PatternTopic(pattern));
        listenerContainer.setConnectionFactory(connectionFactory);
        RedisConnection connection = connectionFactory.getConnection();
        connection.pSubscribe(listener, pattern.getBytes());
        log.info("开启订阅，订阅pattern为"+pattern);
    }
}
