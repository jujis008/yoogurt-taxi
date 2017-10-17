package com.yoogurt.taxi.notification.service;

import com.yoogurt.taxi.dal.beans.Message;
import com.yoogurt.taxi.dal.condition.notification.MessageCondition;

import java.util.List;

public interface MessageService {

    Message addMessage(Message message);

    int addMessages(List<Message> messages);

    List<Message> getMessages(MessageCondition condition);

}
