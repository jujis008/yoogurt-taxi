package com.yoogurt.taxi.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.bo.DateTimeSection;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.helper.RedisHelper;
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
import com.yoogurt.taxi.dal.condition.order.RentWebListCondition;
import com.yoogurt.taxi.dal.enums.RentStatus;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.dao.RentDao;
import com.yoogurt.taxi.order.form.RentCancelForm;
import com.yoogurt.taxi.order.form.RentForm;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RentInfoServiceImpl extends AbstractOrderBizService implements RentInfoService {

    @Autowired
    private PagerFactory webPagerFactory;

    @Autowired
    private PagerFactory appPagerFactory;

    @Autowired
    private RestUserService userService;

    @Autowired
    private RentDao rentDao;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<RentInfoModel> getRentList(RentPOICondition condition) {

        return rentDao.getRentList(condition);
    }

    @Override
    public Pager<RentInfoModel> getRentListByPage(RentListCondition condition) {

        Page<RentInfoModel> page = rentDao.getRentListByPage(condition);
        if (!condition.isFromApp()) {
            return webPagerFactory.generatePager(page);
        }
        return appPagerFactory.generatePager(page);
    }

    @Override
    public List<RentInfo> getRentInfoList(String userId, String orderId, Integer pageNum, Integer pageSize, Integer... status) {
        Example ex = new Example(RentInfo.class);
        Example.Criteria criteria = ex.createCriteria();
        criteria.andEqualTo("isDeleted", Boolean.FALSE).andEqualTo("userId", userId).andIn("status", Arrays.asList(status));
        if (StringUtils.isNotBlank(orderId)) {
            criteria.andEqualTo("rentId",orderId);
        }
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize, "gmt_create DESC");
        }
        return rentDao.selectByExample(ex);
    }


    /**
     * 获取租单信息
     *
     * @param rentId 租单信息ID
     * @param userId 发单人ID
     * @return 租单信息
     */
    @Override
    public RentInfo getRentInfo(String rentId, String userId) {
        if (StringUtils.isNotBlank(rentId)) {
            RentInfo rentInfo = rentDao.selectById(rentId);
            if (rentInfo == null) return null;
            if (StringUtils.isNotBlank(userId) && !userId.equals(rentInfo.getUserId())) return null;
            return rentInfo;
        }
        return null;
    }

    @Override
    public ResponseObj addRentInfo(RentForm rentForm) {
        if (rentForm.getGiveBackTime().getTime() - rentForm.getHandoverTime().getTime() < Constants.MIN_HOURS * 3600000) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "交车时间与还车时间必须间隔" + Constants.MIN_HOURS + "小时以上");
        }
        ResponseObj obj = buildRentInfo(rentForm);
        if (obj.isSuccess()) {
            RentInfo rentInfo = (RentInfo) obj.getBody();
            rentDao.insertSelective(rentInfo);

            //加入redis超时取消消息队列
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime handoverDateTime = LocalDateTime.ofInstant(rentInfo.getHandoverTime().toInstant(), ZoneId.systemDefault());
            long seconds = Duration.between(currentDateTime, handoverDateTime).getSeconds();
            redisHelper.set(CacheKey.MESSAGE_ORDER_TIMEOUT_KEY+rentInfo.getRentId(),rentInfo.getRentId(),seconds);
        }
        return obj;
    }

    @Override
    public RentInfo cancel(RentCancelForm cancelForm) {

        RentInfo rentInfo = getRentInfo(cancelForm.getRentId(), cancelForm.getUserId());
        if (rentInfo == null) return null;
        if (!RentStatus.WAITING.getCode().equals(rentInfo.getStatus())) return null;
        rentInfo.setStatus(RentStatus.CANCELED.getCode());
        if (modifyStatus(cancelForm.getRentId(), RentStatus.CANCELED)) {
            redisHelper.del(CacheKey.MESSAGE_ORDER_TIMEOUT_KEY+cancelForm.getRentId());
            return rentInfo;
        }
        return null;
    }

    /**
     * 租单超时自动取消
     *
     * @param rentId
     * @return
     */
    @Override
    public RentInfo cancelOverdue(String rentId) {
        RentInfo rentInfo = getRentInfo(rentId, null);
        if (rentInfo == null) return null;
        //如果订单已被操作，则无需再次操作
        if (!rentInfo.getStatus().equals(RentStatus.WAITING.getCode())) return rentInfo;
        rentInfo.setStatus(RentStatus.TIMEOUT.getCode());

        Example example = new Example(RentInfo.class);
        example.createCriteria().andEqualTo(rentId).andEqualTo("status",RentStatus.WAITING.getCode());
        int i = rentDao.updateByExampleSelective(rentInfo, example);
        return i==1?rentInfo:null;
    }

    @Override
    public boolean modifyStatus(String rentId, RentStatus status) {
        if (StringUtils.isBlank(rentId) || status == null) return false;
        RentInfo rentInfo = getRentInfo(rentId, null);
        if (rentInfo == null) return false;
        if (status.getCode().equals(rentInfo.getStatus())) return true; //与原租单状态相同，直接返回true
        if (status.getCode() < rentInfo.getStatus()) return false; //不允许状态回退

        rentInfo.setStatus(status.getCode());
        return rentDao.updateByIdSelective(rentInfo) == 1;
    }

    @Override
    public Pager<RentInfo> getRentListForWebPage(RentWebListCondition condition) {
        PageHelper.startPage(condition.getPageNum(),condition.getPageSize());
        Example example = new Example(RentInfo.class);
        example.setOrderByClause(" gmt_create desc");
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(condition.getName())) {
            criteria.andEqualTo("driverName",condition.getName());
        }
        if (StringUtils.isNotBlank(condition.getPhone())) {
            criteria.andEqualTo("mobile",condition.getPhone());
        }
        if (condition.getOrderId() != null) {
            criteria.andEqualTo("orderId",condition.getOrderId());
        }
        if (condition.getStartTime() != null) {
            criteria.andLessThanOrEqualTo("handoverTime",condition.getStartTime());
        }
        if (condition.getEndTime() != null) {
            criteria.andGreaterThanOrEqualTo("giveBackTime",condition.getEndTime());
        }
        Page<RentInfo> rentInfoList = (Page<RentInfo>) rentDao.selectByExample(example);
        return webPagerFactory.generatePager(rentInfoList);
    }

    @Override
    public OrderModel info(String orderId, String userId) {
        RentInfo rentInfo = getRentInfo(orderId, userId);
        OrderModel model = new OrderModel();
        BeanUtils.copyProperties(rentInfo, model);
        return model;
    }

    /**
     * 指定用户id和租单状态，获取相应的租单列表，并按交车时间升序排列。
     *
     * @param userId 用户id
     * @param status 租单状态，可以传入多个
     * @return 租单列表
     */
    @Override
    public List<RentInfo> getRentList(String userId, Integer... status) {
        Example ex = new Example(RentInfo.class);
        ex.createCriteria()
                .andEqualTo("userId", userId)
                .andIn("status", Arrays.asList(status))
                .andEqualTo("isDeleted", Boolean.FALSE);
        ex.setOrderByClause("handover_time ASC");
        return rentDao.selectByExample(ex);
    }

    /**
     * 校验是否可以发单。
     * 0、交车时间与发单时间至少间隔一个小时，交车时间与还车时间至少间隔8小时；
     * 1、押金余额：充足；
     * 2、不能超过未完成订单数量上限；
     * 3、订单时间不能重叠。
     *
     * @return 校验结果
     */
    private ResponseObj isAllowPublish(RentForm rentForm) {
        String userId = rentForm.getUserId();
        //0. 交车时间与发单时间至少间隔一个小时
        if (rentForm.getHandoverTime().getTime() - new Date().getTime() < Constants.MIN_PUBLISH_INTERVAL_HOURS * 3600000)
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "交车时间与发单时间至少间隔" + Constants.MIN_PUBLISH_INTERVAL_HOURS + "小时");

        if (rentForm.getGiveBackTime().getTime() - rentForm.getHandoverTime().getTime() < Constants.MIN_WORKING_HOURS * 3600000)
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "交车时间与还车时间至少间隔" + Constants.MIN_WORKING_HOURS + "小时");

        ResponseObj obj = super.isAllowed(userId);
        if (!obj.isSuccess()) return obj;

        List<RentInfo> rentList = getRentList(userId, RentStatus.WAITING.getCode(), RentStatus.RENT.getCode());
        if (CollectionUtils.isEmpty(rentList)) //未发布过租单，直接返回成功
            return ResponseObj.success();

        //2. 订单数量上限校验
        if (rentList.size() >= Constants.MAX_RENT_COUNT)
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "最多发布" + Constants.MAX_RENT_COUNT + "笔租单");

        DateTimeSection section = new DateTimeSection(rentForm.getHandoverTime(), rentForm.getGiveBackTime());

        //3.订单时间校验
        for (RentInfo rentInfo : rentList) {
            DateTimeSection sectionA = new DateTimeSection(rentInfo.getHandoverTime(), rentInfo.getGiveBackTime());
            if (DateTimeSection.isInclude(sectionA, section)) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, sectionA.toString() + "已有订单安排，请重新选择");
            }
        }
        //校验通过
        return ResponseObj.success();
    }

    private ResponseObj buildRentInfo(RentForm rentForm) {
        ResponseObj validateResult = isAllowPublish(rentForm);
        //校验不成功，直接返回校验结果
        if (!validateResult.isSuccess()) return validateResult;

        RentInfo rentInfo = new RentInfo(RandomUtils.getPrimaryKey());
        BeanUtils.copyProperties(rentForm, rentInfo);
        String userId = rentForm.getUserId();
        //用户信息
        RestResult<UserInfo> userResult = userService.getUserInfoById(userId);
        if (!userResult.isSuccess()) {
            log.warn("[REST]{}", userResult.getMessage());
            return ResponseObj.of(userResult);
        }
        UserInfo user = userResult.getBody();
        //用户认证状态
        if (!UserStatus.AUTHENTICATED.getCode().equals(user.getStatus())) {
            log.warn("[REST]{}", "该司机未认证或者已冻结");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "该司机未认证或者已冻结");
        }
        //司机信息
        RestResult<DriverInfo> driverResult = userService.getDriverInfoByUserId(userId);
        if (!driverResult.isSuccess()) {
            log.warn("[REST]{}", driverResult.getMessage());
            return ResponseObj.of(driverResult);
        }
        DriverInfo driverInfo = driverResult.getBody();
        rentInfo.setDriverId(driverInfo.getId());

        //如果是车主，需要注入车辆相关信息
        if (user.getType().equals(UserType.USER_APP_OFFICE.getCode())) {
            RestResult<List<CarInfo>> carInfo = userService.getCarInfoByUserId(userId);
            if (!carInfo.isSuccess()) {
                log.warn("[REST]{}", userResult.getMessage());
                return ResponseObj.of(userResult);
            }
            List<CarInfo> carList = carInfo.getBody();
            if (CollectionUtils.isEmpty(carList)) {
                log.info("车主的车辆信息未录入");
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "车主的车辆信息未录入");
            }
            buildCarInfo(rentInfo, carList.get(0));
        }
        //注入用户信息
        buildUserInfo(rentInfo, user);
        return ResponseObj.success(rentInfo);
    }

    private void buildCarInfo(RentInfo rent, CarInfo car) {
        rent.setCarId(car.getId());
        rent.setCompany(car.getCompany());
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
