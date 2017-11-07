package com.yoogurt.taxi.finance.config;

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

    public final static String PAY_DIRECT_EXCHANGE_NAME = "X-Exchange-Pay";

    public final static String PAY_QUEUE_NAME = "X-Queue-Pay";

    public final static String TOPIC = "topic.task.pay";

    public final static String NOTIFY_TOPIC_EXCHANGE_NAME = "X-Exchange-Notify";

    /**
     * 订单支付专用队列
     */
    public final static String PAY_NOTIFY_QUEUE_NAME = "X-Queue-Pay-Notify";

    /**
     * 充值专用队列
     */
    public final static String CHARGE_NOTIFY_QUEUE_NAME = "X-Queue-Charge-Notify";

    /**
     * 回调专用 route key
     */
    private final static String TOPIC_TASK_NOTIFY = "topic.notify.#";

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean("directExchange")
    DirectExchange directExchange() {
        return new DirectExchange(PAY_DIRECT_EXCHANGE_NAME);
    }

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean("topicExchange")
    TopicExchange topicExchange() {
        return new TopicExchange(NOTIFY_TOPIC_EXCHANGE_NAME);
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
     * @param payQueue       消息队列
     * @param directExchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding payQueueBinding(Queue payQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(payQueue).to(directExchange).with(TOPIC);
    }

    /**
     * 创建支付回调消息队列
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
     * @param notifyQueue   消息队列
     * @param topicExchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding notifyQueueBinding(Queue notifyQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(notifyQueue).to(topicExchange).with(TOPIC_TASK_NOTIFY);
    }

    /**
     * 创建充值回调消息队列
     *
     * @return Queue
     */
    @Bean("chargeNotifyQueue")
    Queue chargeNotifyQueue() {
        return new Queue(CHARGE_NOTIFY_QUEUE_NAME, true);
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
        return BindingBuilder.bind(chargeNotifyQueue).to(topicExchange).with(TOPIC_TASK_NOTIFY);
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

    /**
     * 订单支付回调TOPIC
     * @return TOPIC
     */
    public static String getPaymentNotifyTopic() {
        return "topic.notify.pay";
    }

    /**
     * 用户充值回调TOPIC
     * @return TOPIC
     */
    public static String getChargeNotifyTopic() {
        return "topic.notify.charge";
    }
}