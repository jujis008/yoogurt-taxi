package com.yoogurt.taxi.pay.mq;

/**
 * 任务接收者，用于任务执行。
 */
public interface TaskReceiver<T> {

    void receive(T t);

}
