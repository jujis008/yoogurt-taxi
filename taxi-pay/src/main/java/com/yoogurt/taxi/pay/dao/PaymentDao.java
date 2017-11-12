package com.yoogurt.taxi.pay.dao;

import com.yoogurt.taxi.pay.doc.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentDao extends MongoRepository<Payment, String> {


}