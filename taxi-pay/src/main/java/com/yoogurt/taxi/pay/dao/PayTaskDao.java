package com.yoogurt.taxi.pay.dao;

import com.yoogurt.taxi.pay.doc.PayTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PayTaskDao extends MongoRepository<PayTask, String> {
}
