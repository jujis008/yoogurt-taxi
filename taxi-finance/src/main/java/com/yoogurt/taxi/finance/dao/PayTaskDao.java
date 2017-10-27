package com.yoogurt.taxi.finance.dao;

import com.yoogurt.taxi.finance.task.PayTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PayTaskDao extends MongoRepository<PayTask, String> {
}
