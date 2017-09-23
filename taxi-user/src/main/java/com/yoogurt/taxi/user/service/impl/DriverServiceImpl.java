package com.yoogurt.taxi.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.factory.WebPagerFactory;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;
import com.yoogurt.taxi.dal.model.user.DriverWLModel;
import com.yoogurt.taxi.user.dao.DriverDao;
import com.yoogurt.taxi.user.service.DriverService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService{
    @Autowired
    private DriverDao   driverDao;
    @Autowired
    private WebPagerFactory webPagerFactory;
    @Override
    public ResponseObj saveDriverInfo(DriverInfo driverInfo) {
        driverDao.insert(driverInfo);
        return ResponseObj.success();
    }

    @Override
    public DriverInfo getDriverInfo(Long driverId) {
        DriverInfo driverInfo = driverDao.selectById(driverId);
        return driverInfo;
    }

    @Override
    public ResponseObj getDriverWebList(DriverWLCondition condition) {
        PageHelper.startPage(condition.getPageNum(),condition.getPageSize(),"gmt_modify desc");
        Page<DriverWLModel> driverWebList = driverDao.getDriverWebList(condition);
        return ResponseObj.success(webPagerFactory.generatePager(driverWebList));
    }

    @Override
    public ResponseObj removeDriver(Long driverId) {
        DriverInfo driverInfo = driverDao.selectById(driverId);
        if(driverInfo == null) {
            return ResponseObj.success();
        }
        driverInfo.setIsDeleted(Boolean.TRUE);
        driverDao.updateById(driverInfo);
        return ResponseObj.success();
    }

    @Override
    public DriverInfo getDriverByUserId(Long userId) {
        Example example = new Example(DriverInfo.class);
        example.createCriteria().andEqualTo("userId",userId);
        List<DriverInfo> driverInfoList = driverDao.selectByExample(example);
        if (CollectionUtils.isEmpty(driverInfoList)) {
            return null;
        }
        return driverInfoList.get(0);
    }
}
