package com.yoogurt.taxi.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;
import com.yoogurt.taxi.dal.condition.order.RentPOICondition;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.dao.RentDao;
import com.yoogurt.taxi.order.form.RentForm;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RentInfoServiceImpl implements RentInfoService {

    @Autowired
    private PagerFactory webPagerFactory;

    @Autowired
    private RestUserService userService;

    @Autowired
    private RentDao rentDao;

    @Override
    public List<RentInfoModel> getRentList(RentPOICondition condition) {

        return rentDao.getRentList(condition.getMaxLng(), condition.getMinLng(), condition.getMaxLat(), condition.getMinLat(),
                condition.getStartTime(), condition.getEndTime(), condition.likes());
    }

    @Override
    public Pager<RentInfoModel> getRentListByPage(RentListCondition condition) {
        String orderBy = "";
        if (StringUtils.isNotBlank(condition.getSortName())) {
            orderBy += condition.getSortName();
        }
        orderBy += " ";
        if(StringUtils.isNotBlank(condition.getSortOrder())) {
            orderBy += condition.getSortOrder();
        }
        //没有传入排序字段
        if (StringUtils.isBlank(orderBy)) {
            //默认按发布时间倒序排列
            orderBy += "r.gmt_create DESC";
        }
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize(), orderBy);
        Page<RentInfoModel> page = rentDao.getRentListByPage(condition.getMaxLng(), condition.getMinLng(), condition.getMaxLat(), condition.getMinLat(),
                condition.getStartTime(), condition.getEndTime(), condition.likes(), condition.getSortName(), condition.getSortOrder());
        return webPagerFactory.generatePager(page);
    }

    @Override
    public RentInfo getRentInfo(Long rentId) {
        if (rentId != null && rentId > 0) {
            return rentDao.selectById(rentId);
        }
        return null;
    }

    @Override
    public ResponseObj addRentInfo(RentForm rentForm) {

        ResponseObj obj = buildRentInfo(rentForm);
        if (obj.isSuccess()) {
            rentDao.insertSelective((RentInfo) obj.getBody());
        }
        return obj;
    }

    private ResponseObj buildRentInfo(RentForm rentForm) {
        RentInfo rentInfo = new RentInfo(RandomUtils.getPrimaryKey());
        BeanUtils.copyProperties(rentForm, rentInfo);
        Long userId = rentForm.getUserId();
        RestResult<UserInfo> userResult = userService.getUserInfoById(userId);
        if (!userResult.isSuccess()) {
            log.warn("[REST]{}", userResult.getMessage());
            return ResponseObj.of(userResult);
        }
        RestResult<DriverInfo> driverResult = userService.getDriverInfoByUserId(userId);
        if (!driverResult.isSuccess()) {
            log.warn("[REST]{}", driverResult.getMessage());
            return ResponseObj.of(driverResult);
        }
        DriverInfo driverInfo = driverResult.getBody();
        rentInfo.setDriverId(driverInfo.getId());
        UserInfo user = userResult.getBody();
        if(user != null) {
            //如果是正式司机，需要注入车辆相关信息
            if (user.getType().equals(UserType.USER_APP_OFFICE.getCode())) {
                RestResult<List<CarInfo>> carInfo = userService.getCarInfoByUserId(userId);
                if (!carInfo.isSuccess()) {
                    log.warn("[REST]{}", userResult.getMessage());
                    return ResponseObj.of(userResult);
                }
                List<CarInfo> carList = carInfo.getBody();
                if (CollectionUtils.isEmpty(carList)) {
                    log.info("正式司机的车辆信息未录入");
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "正式司机的车辆信息未录入");
                }
                buildCarInfo(rentInfo, carList.get(0));
            }
            //注入用户信息
            buildUserInfo(rentInfo, user);
            return ResponseObj.success(rentInfo);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户不存在");
    }

    private void buildCarInfo(RentInfo rent, CarInfo car) {
        rent.setCarId(car.getId());
        rent.setPlateNumber(car.getPlateNumber());
        rent.setEnergyType(car.getEnergyType());
        rent.setVehicleType(car.getVehicleType());
        rent.setCarThumb(car.getCarPicture());
        rent.setDriverId(car.getDriverId());
        rent.setVin(car.getVin());
    }

    private void buildUserInfo(RentInfo rent, UserInfo user) {
        rent.setDriverName(user.getName());
        rent.setMobile(user.getUsername());
        rent.setAvatar(user.getAvatar());
        rent.setUserType(user.getType());
    }
}
