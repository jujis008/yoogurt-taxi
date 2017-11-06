package com.yoogurt.taxi.notification;

import com.google.gson.Gson;
import com.yoogurt.taxi.dal.condition.notification.MessageCondition;
import com.yoogurt.taxi.dal.doc.notification.Message;
import com.yoogurt.taxi.notification.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MessageTests {

    @Autowired
    private MsgService msgService;

    @Test
    public void addMessageTest() {
        Message message = new Message();
//        message.setMessageId(1283193L);
        message.setToUserId(8889L);
        message.setTitle("这里是标题");
        message.setContent("这里是内容");
        message.setStatus(10);
//        message.setType(10);
        Message result = msgService.addMessage(message);
        Assert.assertNotNull("添加消息失败", result);
    }

    @Test
    public void listMessageTest() {
        MessageCondition condition = new MessageCondition();
        condition.setUserId(8888L);
        List<Message> messages = msgService.getMessages(condition);
        Gson gson = new Gson();
        log.info(gson.toJson(messages));
    }

}
