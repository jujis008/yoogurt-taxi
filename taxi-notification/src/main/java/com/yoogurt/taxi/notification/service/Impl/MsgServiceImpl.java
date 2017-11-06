package com.yoogurt.taxi.notification.service.Impl;

import com.yoogurt.taxi.dal.condition.notification.MessageCondition;
import com.yoogurt.taxi.dal.doc.notification.Message;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.notification.dao.MsgDao;
import com.yoogurt.taxi.notification.service.MsgService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsgServiceImpl implements MsgService {

    @Autowired
    private MsgDao msgDao;

    @Override
    public Message addMessage(Message message) {
        if (message == null) return null;
        return msgDao.insert(message);
    }

    @Override
    public int addMessages(List<Message> messages) {
        if (CollectionUtils.isEmpty(messages)) return 0;
        return msgDao.insert(messages).size();
    }

    @Override
    public List<Message> getMessages(MessageCondition condition) {
        Message message = new Message();
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (condition.getType() != null) {
            message.setSendType(SendType.getEnumByCode(condition.getType()));
            matcher.withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        if (condition.getUserId() != null) {
            message.setToUserId(condition.getUserId());
            matcher.withMatcher("toUserId", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<Message> example = Example.of(message,matcher);
        return msgDao.findAll(example, new Sort(Sort.Direction.DESC, "gmtCreate"));
    }
}
