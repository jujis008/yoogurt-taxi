package com.yoogurt.taxi.system.service;

import com.yoogurt.taxi.common.vo.ResponseObj;

public interface PhoneCodeService {
    ResponseObj sendPhoneCode(String phoneNumber, String phoneCode);
    ResponseObj validPhoneCode(String phoneNumber, String phoneCode);
}
