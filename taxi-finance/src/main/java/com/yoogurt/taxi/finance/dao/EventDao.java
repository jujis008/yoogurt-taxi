package com.yoogurt.taxi.finance.dao;

import com.yoogurt.taxi.dal.doc.finance.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventDao extends MongoRepository<Event, String> {
}
