package com.yoogurt.taxi.pay.mq;

import org.springframework.stereotype.Service;

@Service
public interface TaskSender<T> {

    void send(T task);
}
