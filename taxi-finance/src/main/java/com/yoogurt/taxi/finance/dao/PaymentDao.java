package com.yoogurt.taxi.finance.dao;

import com.yoogurt.taxi.dal.doc.finance.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentDao extends MongoRepository<Payment, String> {


}