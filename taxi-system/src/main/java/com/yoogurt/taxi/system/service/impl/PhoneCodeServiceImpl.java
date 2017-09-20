package com.yoogurt.taxi.system.service.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RegUtil;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.system.service.PhoneCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class PhoneCodeServiceImpl implements PhoneCodeService{
    @Autowired
    private RedisHelper redisHelper;
    @Override
    public ResponseObj sendPhoneCode(String phoneNumber, String phoneCode) {
        if (RegUtil.checkMobile(phoneNumber)) {
            redisHelper.set(CacheKey.VERIFY_CODE_KEY+phoneNumber,phoneCode, Constants.VERIFY_CODE_EXPIRE_SECONDS);
            //TODO 接入手机通道
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"号码格式错误");
    }

    @Override
    public ResponseObj validPhoneCode(String phoneNumber, String phoneCode) {
        if (!RegUtil.checkMobile(phoneNumber)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"号码格式错误");
        }
        if(StringUtils.isBlank(phoneCode)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"短信验证码不能为空");
        }
        if (!phoneCode.equals(redisHelper.get(CacheKey.VERIFY_CODE_KEY+phoneNumber))) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"验证码错误");
        }
        return ResponseObj.success();
    }
}
