package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.finance.task.PayTask;

public interface PayTaskService {

    PayTask getPayTask(String taskId);

    PayTask addPayTask(PayTask task);

    PayTask savePayTask(PayTask task);

    boolean deletePayTask(String taskId);

}
