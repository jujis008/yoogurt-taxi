package com.yoogurt.taxi.notification.service.Impl;

import com.yoogurt.taxi.dal.beans.Message;
import com.yoogurt.taxi.dal.condition.notification.MessageCondition;
import com.yoogurt.taxi.notification.dao.MessageDao;
import com.yoogurt.taxi.notification.service.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Override
    public Message addMessage(Message message) {
        if(message == null) return null;
        if(messageDao.insertSelective(message) == 1) return message;
        return null;
    }

    @Override
    public List<Message> getMessages(MessageCondition condition) {
        Example ex = new Example(Message.class);
        Example.Criteria criteria = ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE);
        if (condition.getUserId() != null) {

            criteria.andEqualTo("toUserId", condition.getUserId());
        }
        if (condition.getType() != null) {

            criteria.andEqualTo("type", condition.getType());
        }
        if (StringUtils.isNoneBlank(condition.getKeywords())) {
            criteria.andLike("title", condition.likes());
        }
        return messageDao.selectByExample(ex);
    }
}
