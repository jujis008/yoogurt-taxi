package com.yoogurt.taxi.notification.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.Message;
import com.yoogurt.taxi.dal.mapper.MessageMapper;

public interface MessageDao extends IDao<MessageMapper, Message> {
}
