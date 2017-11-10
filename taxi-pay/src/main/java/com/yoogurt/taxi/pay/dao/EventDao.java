package com.yoogurt.taxi.pay.dao;

import com.yoogurt.taxi.dal.doc.finance.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventDao extends MongoRepository<Event, String> {
}
