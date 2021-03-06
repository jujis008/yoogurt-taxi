package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserAddress;
import com.yoogurt.taxi.user.dao.UserAddressDao;
import com.yoogurt.taxi.user.service.UserAddressService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressDao userAddressDao;

    @Override
    public ResponseObj getUserAddressListByUserId(String userId, String keywords) {
        Example example = new Example(UserAddress.class);
        example.setOrderByClause("is_primary desc,gmt_modify desc");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("isDeleted",Boolean.FALSE);
        if (StringUtils.isNotBlank(keywords)) {
            criteria.andEqualTo("address", keywords);
        }
        return ResponseObj.success(userAddressDao.selectByExample(example));
    }

    @Override
    public ResponseObj saveUserAddress(UserAddress userAddress) {
        if (userAddress.getIsPrimary()) {
            Example example = new Example(UserAddress.class);
            example.createCriteria().andEqualTo("userId",userAddress.getUserId())
                    .andEqualTo("isPrimary",Boolean.TRUE)
                    .andEqualTo("isDeleted",Boolean.FALSE);
            List<UserAddress> userAddresses = userAddressDao.selectByExample(example);
            if (CollectionUtils.isNotEmpty(userAddresses)) {
                UserAddress dbUserAddress = userAddresses.get(0);
                dbUserAddress.setIsPrimary(Boolean.FALSE);
                userAddresses.forEach(e->userAddressDao.updateByIdSelective(e));
            }
        }
        if (userAddress.getId() == null) {
            userAddressDao.insert(userAddress);
            return ResponseObj.success();
        }
        userAddressDao.updateByIdSelective(userAddress);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj removeUserAddress(Long id) {
        UserAddress userAddress = userAddressDao.selectById(id);
        if (userAddress == null) {
            return ResponseObj.success();
        }
        userAddressDao.deleteById(id);
        return ResponseObj.success();
    }

    @Override
    public UserAddress getUserAddressById(Long id) {
        return userAddressDao.selectById(id);
    }
}
