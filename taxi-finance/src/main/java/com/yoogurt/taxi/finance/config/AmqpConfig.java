package com.yoogurt.taxi.finance.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    private final static String PAY_EXCHANGE_NAME = "X-Exchange-Pay";

    private final static String PAY_QUEUE_NAME = "X-Queue-Pay";

    private final static String TOPIC = "topic.task.#";

    /**
     * 创建消息队列
     *
     * @return Queue
     */
    @Bean
    Queue queue() {
        return new Queue(PAY_QUEUE_NAME, true);
    }

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(PAY_EXCHANGE_NAME);
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
        return BindingBuilder.bind(queue).to(exchange).with(TOPIC);
    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setQueue(PAY_QUEUE_NAME);
        rabbitTemplate.setExchange(PAY_EXCHANGE_NAME);
        //默认用的是SimpleMessageConverter，这对于复杂对象（对象中会嵌套其他对象）的情况下，会出现序列化问题
        //改用Jackson，实际上是把对象序列化成JSON格式，较为通用
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * issue: no method found for class [b
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}
