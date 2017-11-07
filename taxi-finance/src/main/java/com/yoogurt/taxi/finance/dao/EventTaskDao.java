package com.yoogurt.taxi.finance.dao;


import com.yoogurt.taxi.dal.doc.finance.EventTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventTaskDao extends MongoRepository<EventTask, String> {
}
