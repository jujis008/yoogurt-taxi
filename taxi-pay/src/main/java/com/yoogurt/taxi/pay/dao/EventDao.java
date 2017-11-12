package com.yoogurt.taxi.pay.dao;

import com.yoogurt.taxi.pay.doc.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventDao extends MongoRepository<Event, String> {
}
