package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.finance.task.PayTask;

import java.util.concurrent.CompletableFuture;

public interface PayChannelService {

    CompletableFuture<ResponseObj> doTask(PayTask payTask);
}