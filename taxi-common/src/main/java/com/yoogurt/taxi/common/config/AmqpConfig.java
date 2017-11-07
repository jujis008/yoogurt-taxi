package com.yoogurt.taxi.common.config;

import com.yoogurt.taxi.common.converter.HessianMessageConverter;
import com.yoogurt.taxi.common.enums.MessageQueue;
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

    /************************  支付  *****************************/
    /**
     * 支付交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean("payExchange")
    DirectExchange getPayExchange() {
        return new DirectExchange(MessageQueue.getPayExchange());
    }

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean("notifyExchange")
    TopicExchange getNotifyExchange() {
        return new TopicExchange(MessageQueue.getNotifyExchange());
    }

    /**
     * 创建支付消息队列
     *
     * @return Queue
     */
    @Bean("payQueue")
    Queue getPayQueue() {
        return new Queue(MessageQueue.PAY_QUEUE.getQueue(), true);
    }

    /**
     * 交换机和支付消息队列的绑定，并指定route key
     *
     * @param payQueue    消息队列
     * @param payExchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding payQueueBinding(Queue payQueue, DirectExchange payExchange) {
        return BindingBuilder.bind(payQueue).to(payExchange).with(MessageQueue.PAY_QUEUE.getRoutingKey());
    }

    /**
     * 创建支付回调消息队列
     *
     * @return Queue
     */
    @Bean("notifyQueue")
    Queue getOrderNotifyQueue() {
        return new Queue(MessageQueue.ORDER_NOTIFY_QUEUE.getQueue(), true);
    }

    /**
     * 交换机和回调消息队列的绑定，并指定route key
     *
     * @param notifyQueue    消息队列
     * @param notifyExchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding orderNotifyQueueBinding(Queue notifyQueue, TopicExchange notifyExchange) {
        return BindingBuilder.bind(notifyQueue).to(notifyExchange).with(MessageQueue.ORDER_NOTIFY_QUEUE.getRoutingKey());
    }

    /**
     * 创建充值回调消息队列
     *
     * @return Queue
     */
    @Bean("chargeNotifyQueue")
    Queue getChargeNotifyQueue() {
        return new Queue(MessageQueue.CHARGE_NOTIFY_QUEUE.getQueue(), true);
    }

    /**
     * 交换机和回调消息队列的绑定，并指定route key
     *
     * @param chargeNotifyQueue 消息队列
     * @param notifyExchange    消息交换机
     * @return Binding
     */
    @Bean
    Binding chargeNotifyQueueBinding(Queue chargeNotifyQueue, TopicExchange notifyExchange) {
        return BindingBuilder.bind(chargeNotifyQueue).to(notifyExchange).with(MessageQueue.CHARGE_NOTIFY_QUEUE.getRoutingKey());
    }
    /************************** -- The End -- ********************************/


    /****************************** 通知 *************************************/
    /**
     * 创建消息队列
     *
     * @return Queue
     */
    @Bean("notificationQueue")
    Queue getNotificationQueue() {
        return new Queue(MessageQueue.ORDER_NOTIFICATION_QUEUE.getQueue(), true);
    }

    @Bean("smsQueue")
    Queue getSmsQueue() {
        return new Queue(MessageQueue.SMS_NOTIFICATION_QUEUE.getQueue(), true);
    }

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean("notificationExchange")
    TopicExchange getNotificationExchange() {
        return new TopicExchange(MessageQueue.getNotificationExchange());
    }

    /**
     * 交换机和队列的绑定，并指定route key
     *
     * @param notificationQueue    消息队列
     * @param notificationExchange 消息交换机
     * @return Binding
     */
    @Bean
    Binding notificationQueueBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(MessageQueue.ORDER_NOTIFICATION_QUEUE.getRoutingKey());
    }

    @Bean
    Binding smsQueueBinding(Queue smsQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue).to(notificationExchange).with(MessageQueue.SMS_NOTIFICATION_QUEUE.getRoutingKey());
    }
    /************************** -- The End -- ********************************/


    /**
     * 自定义RabbitTemplate，主要是改造MessageConverter。
     * 默认用的是SimpleMessageConverter，这对于复杂对象（对象中会嵌套其他对象）的情况下，会出现序列化问题。
     * 改用Hessian，实际上是把对象序列化成二进制，较为通用。
     * 不建议使用JDK默认提供的序列化工具。
     *
     * @return RabbitTemplate
     */
    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
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