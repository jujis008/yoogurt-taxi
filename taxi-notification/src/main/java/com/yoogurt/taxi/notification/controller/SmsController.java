package com.yoogurt.taxi.notification.controller;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.SmsPayload;
import com.yoogurt.taxi.dal.enums.SmsTemplateType;
import com.yoogurt.taxi.dal.model.ucpaas.TemplateSms;
import com.yoogurt.taxi.notification.config.SmsConfig;
import com.yoogurt.taxi.notification.mq.SmsSender;
import com.yoogurt.taxi.notification.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/c/notification/sms")
public class SmsController extends BaseController {
    @Autowired
    private SmsSender smsSender;
    @Autowired
    private RedisHelper redisHelper;

    @RequestMapping(value = "/i/sendPhoneCode/phoneNumber/{phoneNumber}",method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj sendPhoneCode(@PathVariable(name = "phoneNumber") String phoneNumber) {
        String param = RandomUtils.getRandNum(6);
        redisHelper.set(CacheKey.VERIFY_CODE_KEY+phoneNumber,param, Constants.VERIFY_CODE_EXPIRE_SECONDS);
        SmsPayload smsPayload = new SmsPayload();
        smsPayload.setType(SmsTemplateType.VALID);
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phoneNumber);
        smsPayload.setPhoneNumbers(phoneNumbers);
        smsPayload.setParam(param);
        smsSender.send(smsPayload);
        return ResponseObj.success(param);
    }

}
