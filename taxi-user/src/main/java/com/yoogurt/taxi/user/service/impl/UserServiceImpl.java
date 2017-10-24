package com.yoogurt.taxi.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.helper.excel.ErrorCellBean;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.BeanUtilsExtends;
import com.yoogurt.taxi.common.utils.DateUtils;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.beans.UserRoleInfo;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.enums.UserFrom;
import com.yoogurt.taxi.dal.enums.UserGender;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.user.UserWLModel;
import com.yoogurt.taxi.user.dao.CarDao;
import com.yoogurt.taxi.user.dao.DriverDao;
import com.yoogurt.taxi.user.dao.UserDao;
import com.yoogurt.taxi.user.dao.UserRoleDao;
import com.yoogurt.taxi.user.form.UserForm;
import com.yoogurt.taxi.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DriverDao driverDao;

    @Autowired
    private PagerFactory webPagerFactory;

    @Autowired
    private CarDao carDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public UserInfo getUserByUserId(Long id) {
        return userDao.selectById(id);
    }

    @Override
    public UserInfo getUserByUsernameAndType(String username, Integer userType) {
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username)
                .andEqualTo("isDeleted",Boolean.FALSE)
                .andEqualTo("type", userType);
        List<UserInfo> userInfoList = userDao.selectByExample(example);
        if (CollectionUtils.isEmpty(userInfoList)) {
            return null;
        }
        return userInfoList.get(0);
    }

    @Override
    public ResponseObj modifyLoginPassword(Long userId, String oldPassword, String newPassword) {
        UserInfo user = userDao.selectById(userId);
        if (user == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "用户不存在");
        }
        if (!Encipher.matches(oldPassword, user.getLoginPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "旧密码错误");
        }
        user.setLoginPassword(Encipher.encrypt(newPassword));
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj modifyHeadPicture(Long userId, String avatar) {
        UserInfo user = userDao.selectById(userId);
        if (user == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "用户不存在");
        }
        user.setAvatar(avatar);
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj resetLoginPwd(String username, String phoneCode, UserType userType, String newPassword) {
        Object cachePhoneCode = redisHelper.get(CacheKey.VERIFY_CODE_KEY + username);
        if (cachePhoneCode == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "验证码过期，请重新获取");
        }
        if (!cachePhoneCode.equals(phoneCode)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "验证码错误");
        }
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("username", username);
        example.createCriteria()
                .andEqualTo("isDeleted", Boolean.FALSE)
                .andEqualTo("type", userType.getCode());
        List<UserInfo> userList = userDao.selectByExample(example);
        if (userList.size() == 0) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "账号有误");
        }
        UserInfo user = userList.get(0);
        user.setLoginPassword(Encipher.encrypt(newPassword));
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj resetLoginPwd(Long userId, String password) {
        UserInfo user = userDao.selectById(userId);
        user.setLoginPassword(Encipher.encrypt(password));
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj payPwdSetting(Long userId, String payPassword) {
        UserInfo user = userDao.selectById(userId);
        if (Encipher.matches(payPassword, user.getLoginPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "交易密码不能和登录密码相同");
        }
        user.setPayPassword(Encipher.encrypt(payPassword));
        userDao.updateById(user);
        modifyUserStatus(userId, UserStatus.UN_AUTHENTICATE);//更改用户状态为
        redisHelper.del(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj modifyPayPwd(Long userId, String oldPassword, String newPassword) {
        UserInfo user = userDao.selectById(userId);
        if (!Encipher.matches(oldPassword, user.getPayPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "旧密码错误");
        }
        if (Encipher.matches(newPassword, user.getLoginPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "交易密码不能和登录密码相同");
        }
        user.setPayPassword(Encipher.encrypt(newPassword));
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj resetPayPwd(String username, String phoneCode, UserType userType, String password) {
        Object cachePhoneCode = redisHelper.get(CacheKey.VERIFY_CODE_KEY + username);
        if (cachePhoneCode == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "验证码过期，请重新获取");
        }
        if (!cachePhoneCode.equals(phoneCode)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "验证码错误");
        }
        Example example = new Example(UserInfo.class);
        example.createCriteria()
                .andEqualTo("isDeleted",Boolean.FALSE)
                .andEqualTo("username", username);
        example.createCriteria().andEqualTo("type", userType.getCode());
        List<UserInfo> userList = userDao.selectByExample(example);
        if (userList.size() == 0) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "账号有误");
        }
        UserInfo user = userList.get(0);
        user.setPayPassword(Encipher.encrypt(password));
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    @Transactional
    public ResponseObj modifyUserName(Long userId, String password, String phoneCode, String phoneNumber) {
        UserInfo user = userDao.selectById(userId);
        if (user == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "账号异常");
        }
        if (phoneNumber.equals(user.getUsername())){
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"请输入新的手机号");
        }
        if (!Encipher.matches(password, user.getLoginPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "密码有误");
        }
        Object cachePhoneCode = redisHelper.get(CacheKey.VERIFY_CODE_KEY + phoneNumber);
        if (cachePhoneCode == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "验证码过期，请重新获取");
        }
        if (!cachePhoneCode.equals(phoneCode)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "验证码错误");
        }
        if(getUserByUsernameAndType(phoneNumber,user.getType()) !=null ){
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"手机号已被使用");
        }
        user.setUsername(phoneNumber);
        List<DriverInfo> driverList = driverDao.getDriverByUserId(userId);
        for (DriverInfo driverInfo:driverList) {
            driverInfo.setMobile(phoneNumber);
            driverDao.updateByIdSelective(driverInfo);
        }
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public void modifyUserStatus(Long userId, UserStatus userStatus) {
        UserInfo user = userDao.selectById(userId);
        user.setStatus(userStatus.getCode());
        userDao.updateById(user);
    }

    @Override
    public ResponseObj removeUser(Long userId) {
        UserInfo user = userDao.selectById(userId);
        if (user != null) {
            user.setIsDeleted(Boolean.TRUE);
            userDao.updateById(user);
        }
        return ResponseObj.success();
    }

    @Override
    public ResponseObj InsertUser(UserInfo userInfo) {
        userDao.insert(userInfo);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj modifyUser(UserInfo userInfo) {
        userDao.updateById(userInfo);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj saveUser(UserForm form) {
        UserInfo userInfo = new UserInfo();
        BeanUtilsExtends.copyProperties(userInfo,form);
        userInfo.setType(UserType.USER_WEB.getCode());
        userInfo.setUserFrom(UserFrom.WEB.getCode());
        userInfo.setLoginPassword(Encipher.encrypt(DigestUtils.md5Hex(form.getLoginPassword())));
        userInfo.setStatus(UserStatus.AUTHENTICATED.getCode());
        if (userInfo.getUserId() == null) {
            userInfo.setUserId(RandomUtils.getPrimaryKey());
            userDao.insert(userInfo);
        } else {
            userDao.updateById(userInfo);
        }
        UserRoleInfo userRoleInfo = new UserRoleInfo();
        userRoleInfo.setUserId(userInfo.getUserId());
        userRoleInfo.setRoleId(form.getRoleId());
        userRoleDao.insert(userRoleInfo);
        return ResponseObj.success();
    }

    @Override
    public Pager<UserWLModel> getUserWebList(UserWLCondition condition) {
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
        Page<UserWLModel> userWebListPage = userDao.getUserWebListPage(condition);
        return webPagerFactory.generatePager(userWebListPage);
    }

    @Transactional
    @Override
    public List<ErrorCellBean> importAgentDriversFromExcel(List<Map<String, Object>> list) {
        List<UserInfo> userInfoList = new ArrayList<>();
        List<DriverInfo> driverInfoList = new ArrayList<>();
        List<Map<String, Object>> phoneCodeList = new ArrayList<>();
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("type", UserType.USER_APP_AGENT.getCode());
        List<UserInfo> dbUserInfoList = userDao.selectByExample(example);
        List<String> dbUsernameList = new ArrayList<>();
        dbUserInfoList.forEach(e -> dbUsernameList.add(e.getUsername()));
        List<ErrorCellBean> errorCellBeanList = new ArrayList<>();
        for (Map<String, Object> map1 : list) {
            Map<String, Object> map = new HashMap<>();
            String phoneNumber = map1.get("phoneNumber").toString();
            String originPassword = RandomUtils.getRandNum(6);
            map.put("phoneNumber", phoneNumber);
            map.put("originPassword", originPassword);
            UserInfo userInfo = new UserInfo();
            Long userId = RandomUtils.getPrimaryKey();
            userInfo.setUserId(userId);
            userInfo.setUsername(phoneNumber);
            userInfo.setName(map1.get("name").toString());
            phoneCodeList.add(map);
            userInfo.setLoginPassword(Encipher.encrypt(DigestUtils.md5Hex(originPassword)));
            userInfo.setStatus(UserStatus.UN_ACTIVE.getCode());
            userInfo.setUserFrom(UserFrom.IMPORT.getCode());
            userInfo.setType(UserType.USER_APP_AGENT.getCode());
            userInfo.setIsDeleted(Boolean.FALSE);
            userInfo.setGmtModify(new Date());
            userInfo.setModifier(0L);
            userInfo.setGmtCreate(new Date());
            userInfo.setCreator(0L);
            userInfoList.add(userInfo);

            DriverInfo driverInfo = new DriverInfo();
            Long driverId = RandomUtils.getPrimaryKey();
            driverInfo.setIdCard(map1.get("idCard").toString());
            driverInfo.setDrivingLicense(map1.get("drivingLicense").toString());
            driverInfo.setUserId(userId);
            driverInfo.setType(UserType.USER_APP_AGENT.getCode());
            driverInfo.setMobile(map1.get("phoneNumber").toString());
            driverInfo.setGender(UserGender.secret.getCode());
            driverInfo.setIsDeleted(Boolean.FALSE);
            driverInfo.setGmtModify(new Date());
            driverInfo.setServiceNumber(map1.get("serviceNumber").toString());
            driverInfo.setModifier(0L);
            driverInfo.setId(driverId);
            driverInfo.setGmtCreate(new Date());
            driverInfo.setCreator(0L);
            driverInfoList.add(driverInfo);

            if (dbUsernameList.contains(phoneNumber)) {
                errorCellBeanList.add(new ErrorCellBean("账号已存在", phoneNumber, list.indexOf(map1) + 2, 1));
            }
            if (!driverInfo.getIdCard().equals(driverInfo.getDrivingLicense())) {
                errorCellBeanList.add(new ErrorCellBean("身份证号和驾驶证号不一致", map1.get("idCard").toString(), list.indexOf(map1) + 2, 1));
            }
        }
        if (!CollectionUtils.isEmpty(errorCellBeanList)) {
            return errorCellBeanList;
        }
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {

            int result = 0;
            result += userDao.batchInsert(userInfoList);
            result += driverDao.batchInsert(driverInfoList);
            //TODO 上线前更换为短信发送
            phoneCodeList.forEach(e -> redisHelper.set(CacheKey.VERIFY_CODE_KEY + e.get("phoneNumber"), e.get("originPassword")));

            return result;
        });
        future.thenAccept(result ->log.info("IMPORT{}", "导入条数：" + result/2));
        return errorCellBeanList;
    }

    @Override
    public List<ErrorCellBean> importOfficeDriversFromExcel(List<Map<String, Object>> list) {
        List<UserInfo> userInfoList = new ArrayList<>();
        List<DriverInfo> driverInfoList = new ArrayList<>();
        List<CarInfo> carInfoList = new ArrayList<>();
        List<Map<String, Object>> phoneCodeList = new ArrayList<>();
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("type", UserType.USER_APP_OFFICE.getCode());
        List<UserInfo> dbUserInfoList = userDao.selectByExample(example);
        List<String> dbUsernameList = new ArrayList<>();
        dbUserInfoList.forEach(e -> dbUsernameList.add(e.getUsername()));
        List<ErrorCellBean> errorCellBeanList = new ArrayList<>();
        for (Map<String, Object> map1 : list) {
            Map<String, Object> map = new HashMap<>();
            String phoneNumber = map1.get("phoneNumber").toString();
            String originPassword = RandomUtils.getRandNum(6);
            map.put("phoneNumber", phoneNumber);
            map.put("originPassword", originPassword);
            UserInfo userInfo = new UserInfo();
            Long userId = RandomUtils.getPrimaryKey();
            userInfo.setUserId(userId);
            userInfo.setUsername(phoneNumber);
            userInfo.setName(map1.get("name").toString());
            phoneCodeList.add(map);
            userInfo.setLoginPassword(Encipher.encrypt(DigestUtils.md5Hex(originPassword)));
            userInfo.setStatus(UserStatus.UN_ACTIVE.getCode());
            userInfo.setUserFrom(UserFrom.IMPORT.getCode());
            userInfo.setType(UserType.USER_APP_OFFICE.getCode());
            userInfo.setIsDeleted(Boolean.FALSE);
            userInfo.setGmtModify(new Date());
            userInfo.setModifier(0L);
            userInfo.setGmtCreate(new Date());
            userInfo.setCreator(0L);
            userInfoList.add(userInfo);

            Long driverId = RandomUtils.getPrimaryKey();
            DriverInfo driverInfo = new DriverInfo();
            driverInfo.setId(driverId);
            driverInfo.setIdCard(map1.get("idCard").toString());
            driverInfo.setDrivingLicense(map1.get("drivingLicense").toString());
            driverInfo.setUserId(userId);
            driverInfo.setType(UserType.USER_APP_OFFICE.getCode());
            driverInfo.setMobile(map1.get("phoneNumber").toString());
            driverInfo.setServiceNumber(map1.get("serviceNumber").toString());
            driverInfo.setGender(UserGender.secret.getCode());
            driverInfo.setIsDeleted(Boolean.FALSE);
            driverInfo.setGmtModify(new Date());
            driverInfo.setModifier(0L);
            driverInfo.setGmtCreate(new Date());
            driverInfo.setCreator(0L);
            driverInfoList.add(driverInfo);

            CarInfo carInfo = new CarInfo();
            carInfo.setIsAuthentication(Boolean.FALSE);
            carInfo.setVehicleType(map1.get("vehicleType").toString());
            carInfo.setVehicleRegisterTime(DateUtils.strToDate(map1.get("vehicleRegisterTime").toString(), "yyyy-MM-dd"));
            carInfo.setUserId(userId);
            carInfo.setPlateNumber(map1.get("plateNumber").toString());
            carInfo.setCompany(map1.get("company").toString());
            carInfo.setDriverId(driverId);
            carInfo.setIsDeleted(Boolean.FALSE);
            carInfo.setGmtModify(new Date());
            carInfo.setModifier(0L);
            carInfo.setGmtCreate(new Date());
            carInfo.setCreator(0L);
            carInfoList.add(carInfo);

            if (dbUsernameList.contains(phoneNumber)) {
                errorCellBeanList.add(new ErrorCellBean("账号已存在", phoneNumber, list.indexOf(map1) + 2, 1));
            }
            if (!driverInfo.getIdCard().equals(driverInfo.getDrivingLicense())) {
                errorCellBeanList.add(new ErrorCellBean("身份证号和驾驶证号不一致", map1.get("idCard").toString(), list.indexOf(map1) + 2, 1));
            }
        }
        if (!CollectionUtils.isEmpty(errorCellBeanList)) {
            return errorCellBeanList;
        }
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {

            int result = 0;
            result +=userDao.batchInsert(userInfoList);
            result +=driverDao.batchInsert(driverInfoList);
            result += carDao.batchInsert(carInfoList);
            //TODO 上线前更换为短信发送
            phoneCodeList.forEach(e -> redisHelper.set(CacheKey.VERIFY_CODE_KEY + e.get("phoneNumber"), e.get("originPassword")));
            return result;
        });
        future.thenAccept(result ->log.info("IMPORT{}", "导入条数：" + result/3));
        return errorCellBeanList;
    }
}
