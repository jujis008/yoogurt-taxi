package com.yoogurt.taxi.pay.dao;


import com.yoogurt.taxi.pay.doc.EventTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventTaskDao extends MongoRepository<EventTask, String> {
}
