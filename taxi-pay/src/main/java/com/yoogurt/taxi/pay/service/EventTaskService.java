package com.yoogurt.taxi.pay.service;


import com.yoogurt.taxi.dal.doc.finance.EventTask;

public interface EventTaskService {

    EventTask getEventTask(String taskId);

    EventTask addEventTask(EventTask task);

    EventTask saveEventTask(EventTask task);

    boolean deleteEventTask(String taskId);
}
