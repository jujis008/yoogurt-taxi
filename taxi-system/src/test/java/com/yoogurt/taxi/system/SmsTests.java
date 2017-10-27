package com.yoogurt.taxi.system;

import com.yoogurt.taxi.dal.model.ucpaas.TemplateSms;
import com.yoogurt.taxi.system.mq.SmsSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTests {

    @Autowired
    private SmsSender sender;

    @Test
    public void sendTest() {
        TemplateSms template = new TemplateSms();
        template.setAppId("6c549ca44a334201b6410884a32472d9");
        template.setTemplateId("155770");
        template.setTo("17364517747");
        template.setParam("055562");
        sender.send(template);
    }
}
