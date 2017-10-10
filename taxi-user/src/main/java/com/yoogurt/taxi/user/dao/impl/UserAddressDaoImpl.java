package com.yoogurt.taxi.user.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.UserAddress;
import com.yoogurt.taxi.dal.mapper.UserAddressMapper;
import com.yoogurt.taxi.user.dao.UserAddressDao;
import org.springframework.stereotype.Repository;

@Repository
public class UserAddressDaoImpl extends BaseDao<UserAddressMapper,UserAddress> implements UserAddressDao{
}
