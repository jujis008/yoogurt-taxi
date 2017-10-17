package com.yoogurt.taxi.notification;

import com.yoogurt.taxi.dal.beans.Message;
import com.yoogurt.taxi.notification.service.MessageService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageTests {

    @Autowired
    private MessageService messageService;

    @Test
    public void addMessageTest() {
        Message message = new Message();
        message.setToUserId(8888L);
        message.setTitle("这里是标题");
        message.setContent("这里是内容");
        message.setStatus(10);
        message.setType(10);
        Message result = messageService.addMessage(message);
        Assert.assertNotNull("添加消息失败", result);
    }
}
