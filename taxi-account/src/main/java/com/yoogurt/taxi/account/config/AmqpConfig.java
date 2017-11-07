package com.yoogurt.taxi.account.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    private final static String NOTIFICATION_EXCHANGE_NAME = "X-Exchange-Notification";

    private final static String NOTIFICATION_QUEUE_NAME = "X-Queue-Notification";

    private final static String NOTIFICATION_TOPIC = "topic.notification.#";

    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * 创建消息队列
     *
     * @return Queue
     */
    @Bean
    Queue queue() {
        return new Queue(NOTIFICATION_QUEUE_NAME, true);
    }

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE_NAME);
    }

    /**
     * 交换机和队列的绑定，并指定route key
     *
     * @param queue    消息队列
     * @param exchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(NOTIFICATION_TOPIC);
    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //默认用的是SimpleMessageConverter，这对于复杂对象（对象中会嵌套其他对象）的情况下，会出现序列化问题
        //改用Jackson，实际上是把对象序列化成JSON格式，较为通用
        rabbitTemplate.setMessageConverter(new HessianMessageConverter());
        return rabbitTemplate;
    }

    /**
     * issue: no method found for class [B
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new HessianMessageConverter());
        return factory;
    }

}
