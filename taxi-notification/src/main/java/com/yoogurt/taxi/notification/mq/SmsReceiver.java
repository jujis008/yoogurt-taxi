package com.yoogurt.taxi.notification.mq;

import com.yoogurt.taxi.dal.bo.SmsPayload;
import com.yoogurt.taxi.dal.model.ucpaas.TemplateSms;
import com.yoogurt.taxi.notification.config.SmsConfig;
import com.yoogurt.taxi.notification.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RabbitListener(queues = "X-Queue-Notification-SMS")
public class SmsReceiver {
    @Autowired
    private SmsService smsService;
    @Autowired
    private SmsConfig smsConfig;

    @RabbitHandler()
    public void receive(@Payload SmsPayload payload) {
        log.info(DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + "收到消息：" + payload.toString());

        TemplateSms templateSms = new TemplateSms();
        templateSms.setParam(payload.getParam());
        templateSms.setTo(StringUtils.join(payload.getPhoneNumbers(),","));
        switch (payload.getType()) {
            case VALID:
                templateSms.setTemplateId(smsConfig.getValidTemplateId());
                break;
            case agent_pwd:
                templateSms.setTemplateId(smsConfig.getAgentPwdTemplateId());
            case office_pwd:
                templateSms.setTemplateId(smsConfig.getOfficePwdTemplateId());
            default:
                return;
        }
        templateSms.setAppId(smsConfig.getAppId());
        String s = smsService.templateSMS(templateSms);
        log.info("短信发送结果："+s);
    }
}
