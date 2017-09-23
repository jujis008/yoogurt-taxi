package com.yoogurt.taxi.notification.service;

import com.yoogurt.taxi.dal.model.ucpaas.TemplateSms;

public interface SmsService {
    String templateSMS(TemplateSms templateSms);
}
