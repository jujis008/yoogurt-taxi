package com.yoogurt.taxi.finance.config;

import com.yoogurt.taxi.dal.enums.MessageQueue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean("directExchange")
    DirectExchange directExchange() {
        return new DirectExchange(MessageQueue.getPayExchange());
    }

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean("topicExchange")
    TopicExchange topicExchange() {
        return new TopicExchange(MessageQueue.getNotifyExchange());
    }

    /**
     * 创建支付消息队列
     *
     * @return Queue
     */
    @Bean("payQueue")
    Queue payQueue() {
        return new Queue(MessageQueue.PAY_QUEUE.getQueue(), true);
    }

    /**
     * 交换机和支付消息队列的绑定，并指定route key
     *
     * @param payQueue       消息队列
     * @param directExchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding payQueueBinding(Queue payQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(payQueue).to(directExchange).with(MessageQueue.PAY_QUEUE.getRoutingKey());
    }

    /**
     * 创建支付回调消息队列
     *
     * @return Queue
     */
    @Bean("notifyQueue")
    Queue notifyQueue() {
        return new Queue(MessageQueue.ORDER_NOTIFY_QUEUE.getQueue(), true);
    }

    /**
     * 交换机和回调消息队列的绑定，并指定route key
     *
     * @param notifyQueue   消息队列
     * @param topicExchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding notifyQueueBinding(Queue notifyQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(notifyQueue).to(topicExchange).with(MessageQueue.ORDER_NOTIFY_QUEUE.getRoutingKey());
    }

    /**
     * 创建充值回调消息队列
     *
     * @return Queue
     */
    @Bean("chargeNotifyQueue")
    Queue chargeNotifyQueue() {
        return new Queue(MessageQueue.CHARGE_NOTIFY_QUEUE.getQueue(), true);
    }

    /**
     * 交换机和回调消息队列的绑定，并指定route key
     *
     * @param chargeNotifyQueue 消息队列
     * @param topicExchange     消息交换机
     * @return Binding
     */
    @Bean
    Binding chargeNotifyQueueBinding(Queue chargeNotifyQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(chargeNotifyQueue).to(topicExchange).with(MessageQueue.CHARGE_NOTIFY_QUEUE.getRoutingKey());
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
     *
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