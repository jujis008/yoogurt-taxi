package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.task.PayTask;

import java.util.concurrent.CompletableFuture;

public interface PayChannelService {

    CompletableFuture<Payment> doTask(PayTask payTask);
}