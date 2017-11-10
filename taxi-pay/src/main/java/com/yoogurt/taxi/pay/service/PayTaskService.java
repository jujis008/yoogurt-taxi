package com.yoogurt.taxi.pay.service;


import com.yoogurt.taxi.pay.doc.PayTask;

public interface PayTaskService {

    PayTask getPayTask(String taskId);

    PayTask addPayTask(PayTask task);

    PayTask savePayTask(PayTask task);

    boolean deletePayTask(String taskId);

}
