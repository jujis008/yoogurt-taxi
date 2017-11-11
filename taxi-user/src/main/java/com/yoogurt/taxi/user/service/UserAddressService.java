package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserAddress;

public interface UserAddressService {
    ResponseObj getUserAddressListByUserId(String userId, String keywords);
    ResponseObj saveUserAddress(UserAddress userAddress);
    ResponseObj removeUserAddress(Long id);
    UserAddress getUserAddressById(Long id);
}
