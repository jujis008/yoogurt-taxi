package com.yoogurt.taxi.notification.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BatchDao;
import com.yoogurt.taxi.dal.beans.Message;
import com.yoogurt.taxi.dal.mapper.MessageMapper;
import com.yoogurt.taxi.notification.dao.MessageDao;
import org.springframework.stereotype.Repository;

@Repository
public class MessageDaoImpl extends BatchDao<MessageMapper, Message> implements MessageDao {
}
