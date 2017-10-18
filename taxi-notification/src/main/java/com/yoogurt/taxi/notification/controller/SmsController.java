package com.yoogurt.taxi.notification.controller;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.model.ucpaas.TemplateSms;
import com.yoogurt.taxi.notification.config.SmsConfig;
import com.yoogurt.taxi.notification.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/c/notification/sms")
public class SmsController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private SmsConfig smsConfig;
    @Autowired
    private RedisHelper redisHelper;

    @RequestMapping(value = "/i/sendPhoneCode/phoneNumber/{phoneNumber}",method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj sendPhoneCode(@PathVariable(name = "phoneNumber") String phoneNumber) {
        TemplateSms templateSms = new TemplateSms();
        templateSms.setAppId(smsConfig.getAppId());
        templateSms.setTemplateId(smsConfig.getValidTemplateId());
        templateSms.setTo(phoneNumber);
        String param = RandomUtils.getRandNum(6);
        templateSms.setParam(param);
        redisHelper.set(CacheKey.VERIFY_CODE_KEY+phoneNumber,param, Constants.VERIFY_CODE_EXPIRE_SECONDS);
        smsService.templateSMS(templateSms);
        return ResponseObj.success(param);
    }

}
