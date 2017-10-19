package com.yoogurt.taxi.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    private final static String ORDER_EXCHANGE_NAME = "X-Exchange-Order";

    private final static String ORDER_QUEUE_NAME = "X-Queue-Order";

    private final static String ORDER_TOPIC = "topic.order.*";

    /**
     * 创建消息队列
     *
     * @return Queue
     */
    @Bean
    Queue queue() {
        return new Queue(ORDER_QUEUE_NAME, true);
    }

    /**
     * 创建消息交换机（exchange）
     *
     * @return TopicExchange
     */
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(ORDER_EXCHANGE_NAME);
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
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_TOPIC);
    }
}
