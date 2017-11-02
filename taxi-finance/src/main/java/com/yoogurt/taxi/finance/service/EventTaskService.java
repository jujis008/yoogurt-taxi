package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.finance.task.EventTask;

public interface EventTaskService {

    EventTask getEventTask(String taskId);

    EventTask addEventTask(EventTask task);

    EventTask saveEventTask(EventTask task);

    boolean deleteEventTask(String taskId);
}
