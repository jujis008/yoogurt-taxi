package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.order.RentPOICondition;
import com.yoogurt.taxi.dal.model.order.RentPOIModel;
import com.yoogurt.taxi.order.dao.RentDao;
import com.yoogurt.taxi.order.form.RentForm;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentInfoServiceImpl implements RentInfoService {

    @Autowired
    private RestUserService userService;

    @Autowired
    private RentDao rentDao;

    @Override
    public List<RentPOIModel> getRentList(RentPOICondition condition) {

        return rentDao.getRentList(condition.getStartTime(), condition.getEndTime(), condition.likes());
    }

    @Override
    public RentInfo getRentInfo(Long rentId) {
        if (rentId != null && rentId > 0) {
            return rentDao.selectById(rentId);
        }
        return null;
    }

    @Override
    public RentInfo addRentInfo(RentForm rentForm) {

        RentInfo rentInfo = buildRentInfo(rentForm);
        if (rentInfo != null) {
            rentDao.insertSelective(rentInfo);
            return rentInfo;
        }
        return null;
    }

    @Override
    public RentInfo buildRentInfo(RentForm rentForm) {
        RentInfo rentInfo = new RentInfo(RandomUtils.getPrimaryKey());
        BeanUtils.copyProperties(rentForm, rentInfo);
        Long userId = rentForm.getUserId();
        CarInfo carInfo = userService.getCarInfoByUserId(userId);
        UserInfo userInfo = userService.getUserInfoById(userId);
        buildCarInfo(rentInfo, carInfo);
        buildUserInfo(rentInfo, userInfo);
        return rentInfo;
    }

    private void buildCarInfo(RentInfo rent, CarInfo car) {
        rent.setCarId(car.getId());
        rent.setPlateNumber(car.getPlateNumber());
        rent.setEnergyType(car.getEnergyType());
        rent.setVehicleType(car.getVehicleType());
        rent.setCarThumb(car.getCarPicture());
        rent.setDriverId(car.getDriverId());
    }

    private void buildUserInfo(RentInfo rent, UserInfo user) {

        rent.setDriverName(user.getName());
        rent.setMobile(user.getUsername());
        rent.setAvatar(user.getAvatar());
        rent.setUserType(user.getType());
    }
}
