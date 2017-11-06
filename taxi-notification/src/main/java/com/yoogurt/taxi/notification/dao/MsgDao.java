package com.yoogurt.taxi.notification.dao;

import com.yoogurt.taxi.dal.doc.notification.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MsgDao extends MongoRepository<Message, Long> {
}
