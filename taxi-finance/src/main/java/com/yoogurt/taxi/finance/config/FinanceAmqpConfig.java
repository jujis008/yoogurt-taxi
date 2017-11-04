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
public class FinanceAmqpConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    private final static String PAY_EXCHANGE_NAME = "X-Exchange-Pay";

    private final static String PAY_NOTIFY_QUEUE_NAME = "X-Queue-Pay-Notify";

    private final static String PAY_QUEUE_NAME = "X-Queue-Pay";

    private final static String TOPIC = "topic.task.#";

    /** 回调专用 route key */
    private final static String TOPIC_TASK_NOTIFY = "topic.task.notify.#";

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
     * 创建支付消息队列
     *
     * @return Queue
     */
    @Bean("payQueue")
    Queue payQueue() {
        return new Queue(PAY_QUEUE_NAME, true);
    }

    /**
     * 交换机和支付消息队列的绑定，并指定route key
     *
     * @param payQueue    消息队列
     * @param exchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding payQueueBinding(Queue payQueue, TopicExchange exchange) {
        return BindingBuilder.bind(payQueue).to(exchange).with(TOPIC);
    }

    /**
     * 创建回调消息队列
     *
     * @return Queue
     */
    @Bean("notifyQueue")
    Queue notifyQueue() {
        return new Queue(PAY_NOTIFY_QUEUE_NAME, true);
    }

    /**
     * 交换机和回调消息队列的绑定，并指定route key
     *
     * @param notifyQueue    消息队列
     * @param exchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding notifyQueueBinding(Queue notifyQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notifyQueue).to(exchange).with(TOPIC_TASK_NOTIFY);
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
     * issue: no method found for class [B
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
