package com.yoogurt.taxi.order.service;

import org.springframework.data.redis.connection.MessageListener;

public interface ExpiredMessageListener extends MessageListener {
}
