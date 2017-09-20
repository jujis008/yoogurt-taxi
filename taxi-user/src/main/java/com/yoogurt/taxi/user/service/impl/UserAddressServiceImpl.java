package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserAddress;
import com.yoogurt.taxi.user.dao.UserAddressDao;
import com.yoogurt.taxi.user.service.UserAddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressDao userAddressDao;

    @Override
    public ResponseObj getUserAddressListByUserId(Long userId, String keywords) {
        Example example = new Example(UserAddress.class);
        example.setOrderByClause("order by field(is_primary,1,0),gmt_modify desc");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        if (StringUtils.isNotBlank(keywords)) {
            criteria.andLike("address", "%" + keywords + "%");
        }
        return ResponseObj.success(userAddressDao.selectByExample(example));
    }

    @Override
    public ResponseObj saveUserAddress(UserAddress userAddress) {
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
        userAddress.setIsDeleted(Boolean.TRUE);
        return ResponseObj.success();
    }

    @Override
    public UserAddress getUserAddressById(Long id) {
        return userAddressDao.selectById(id);
    }
}
