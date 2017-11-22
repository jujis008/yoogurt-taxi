package com.yoogurt.taxi.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.factory.WebPagerFactory;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWebListCondition;
import com.yoogurt.taxi.dal.model.user.DriverWebListModel;
import com.yoogurt.taxi.user.dao.CarDao;
import com.yoogurt.taxi.user.dao.DriverDao;
import com.yoogurt.taxi.user.dao.UserDao;
import com.yoogurt.taxi.user.service.DriverService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverDao driverDao;
    @Autowired
    private WebPagerFactory webPagerFactory;
    @Autowired
    private UserDao userDao;
    @Autowired
    private CarDao carDao;

    @Override
    public ResponseObj saveDriverInfo(DriverInfo driverInfo) {
        if (driverInfo.getId() == null) {
            driverDao.insert(driverInfo);
        } else {
            driverDao.updateByIdSelective(driverInfo);
        }
        return ResponseObj.success();
    }

    @Override
    public DriverInfo getDriverInfo(String driverId) {
        DriverInfo driverInfo = driverDao.selectById(driverId);
        return driverInfo;
    }

    @Override
    public ResponseObj getDriverWebList(DriverWebListCondition condition) {
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
        Page<DriverWebListModel> driverWebList = driverDao.getDriverWebList(condition);
        return ResponseObj.success(webPagerFactory.generatePager(driverWebList));
    }

    @Override
    public ResponseObj removeDriver(String driverId) {
        DriverInfo driverInfo = driverDao.selectById(driverId);
        if (driverInfo != null) {
            driverInfo.setIsDeleted(Boolean.TRUE);
            driverDao.updateById(driverInfo);
        }
        UserInfo userInfo = userDao.selectById(driverInfo.getUserId());
        if (userInfo != null){
            userInfo.setIsDeleted(Boolean.TRUE);
            userDao.updateById(userInfo);
        }

        Example example = new Example(CarInfo.class);
        example.createCriteria().andEqualTo("driverId",driverId)
                .andEqualTo("isDeleted",Boolean.FALSE);
        List<CarInfo> carInfoList = carDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(carInfoList)) {
            for (CarInfo carInfo:carInfoList) {
                carInfo.setIsDeleted(Boolean.TRUE);
                carDao.updateById(carInfo);
            }
        }
        return ResponseObj.success();
    }

    @Override
    public DriverInfo getDriverByUserId(String userId) {
        Example example = new Example(DriverInfo.class);
        example.createCriteria().andEqualTo("userId", userId)
                .andEqualTo("isDeleted",Boolean.FALSE);
        List<DriverInfo> driverInfoList = driverDao.selectByExample(example);
        if (CollectionUtils.isEmpty(driverInfoList)) {
            return null;
        }
        return driverInfoList.get(0);
    }
}
