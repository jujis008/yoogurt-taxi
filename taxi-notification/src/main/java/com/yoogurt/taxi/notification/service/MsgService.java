package com.yoogurt.taxi.notification.service;

import com.yoogurt.taxi.dal.condition.notification.MessageCondition;
import com.yoogurt.taxi.dal.doc.notification.Message;

import java.util.List;

public interface MsgService {

    Message addMessage(Message message);

    int addMessages(List<Message> messages);

    List<Message> getMessages(MessageCondition condition);
}
