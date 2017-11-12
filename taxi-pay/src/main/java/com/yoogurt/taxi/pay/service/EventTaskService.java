package com.yoogurt.taxi.pay.service;


import com.yoogurt.taxi.pay.doc.EventTask;

public interface EventTaskService {

    EventTask getEventTask(String taskId);

    EventTask addEventTask(EventTask task);

    EventTask saveEventTask(EventTask task);

    boolean deleteEventTask(String taskId);
}
