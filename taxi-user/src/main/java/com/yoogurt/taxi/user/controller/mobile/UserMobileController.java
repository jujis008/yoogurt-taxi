package com.yoogurt.taxi.user.controller.mobile;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.BeanUtilsExtends;
import com.yoogurt.taxi.common.utils.DateUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.UserAddress;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.user.UserSessionModel;
import com.yoogurt.taxi.user.form.*;
import com.yoogurt.taxi.user.service.*;
import com.yoogurt.taxi.user.service.rest.RestOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mobile/user")
public class UserMobileController extends BaseController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private CarService carService;
    @Autowired
    private RestOrderService restOrderService;

    @RequestMapping(value = "/i/login", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj agentLogin(@RequestBody LoginForm loginForm) {
        if (StringUtils.isBlank(loginForm.getUsername())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK.getStatus(), "请填写用户名");
        }
        if (StringUtils.isBlank(loginForm.getPassword())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK.getStatus(), "请填写密码");
        }
        UserType userType = UserType.getEnumsByCode(getUserType());
        if (userType == null) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, "请标识请求来源");
        }
        if (!userType.isAppUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        return loginService.login(loginForm.getUsername(), loginForm.getPassword(), userType);
    }

    @RequestMapping(value = "/i/reset/loginPassword", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj resetLoginPassword(@RequestBody @Valid ResetPasswordForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        UserInfo userInfo = userService.getUserByUsernameAndType(form.getPhoneNumber(), getUserType());
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户不存在");
        }
        return userService.resetLoginPwd(form.getPhoneNumber(), form.getPhoneCode(), UserType.getEnumsByCode(userInfo.getType()), form.getPassword());
    }

    /**
     * 查看用户信息
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getUserInfo() {
        UserInfo userInfo = userService.getUserByUserId(getUserId());
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.NOT_LOGIN);
        }
        Long userId = getUserId();
        UserType userType = UserType.getEnumsByCode(getUserType());
        DriverInfo driverInfo = driverService.getDriverByUserId(userInfo.getUserId());
        RestResult<Map<String, Object>> mapRestResult = restOrderService.statistics(userId);
        if (!mapRestResult.isSuccess()) {
            return ResponseObj.of(mapRestResult);
        }
        UserSessionModel userSessionModel = new UserSessionModel();
        Object comment = mapRestResult.getBody().get("comment");
        if (comment != null) {
            BeanUtilsExtends.copyProperties(userSessionModel, comment);
        }
        Object order = mapRestResult.getBody().get("order");
        if (order != null) {
            BeanUtilsExtends.copyProperties(userSessionModel, order);
        }

        BeanUtilsExtends.copyProperties(userSessionModel, userInfo);
        userSessionModel.setDriverAuthenticated(driverInfo.getIsAuthentication());
        if (userType == UserType.USER_APP_OFFICE) {
            List<CarInfo> carByUsers = carService.getCarByUserId(userId);
            if (CollectionUtils.isNotEmpty(carByUsers)) {
                userSessionModel.setCarAuthenticated(carByUsers.get(0).getIsAuthentication());
            }
        }
        return ResponseObj.success(userSessionModel);
    }

    /**
     * 查看用户激活流程状态
     *
     * @return
     */
    @RequestMapping(value = "/activeStatus", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj activeStatus() {
        Long userId = getUserId();
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (!UserStatus.UN_ACTIVE.getCode().equals(userInfo.getStatus())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户非未激活状态");
        }
        Object o = redisHelper.get(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId);
        if (o == null) {
            return ResponseObj.success(1);
        }
        return ResponseObj.success(Integer.valueOf(o.toString()));
    }

    /**
     * 获取司机身份资料
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/driverIdentity", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj driverIdentity() {
        DriverInfo driverInfo = driverService.getDriverByUserId(getUserId());
        if (driverInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED);
        }
        return ResponseObj.success(driverInfo);
    }

    /**
     * 车辆资料
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/carIdentity", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj carIdentity() {
        List<CarInfo> carInfoList = carService.getCarByUserId(getUserId());
        if (CollectionUtils.isEmpty(carInfoList)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED);
        }
        return ResponseObj.success(carInfoList.get(0));
    }

    /**
     * 获取司机信息
     *
     * @param userId 用户id
     * @return ResponseObj
     */
    @RequestMapping(value = "/i/driverInfo/userId/{userId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getDriverInfo(@PathVariable(name = "userId") Long userId) {
        RestResult<Map<String, Object>> statisticsRestResult = restOrderService.statistics(userId);
        if (!statisticsRestResult.isSuccess()) {
            return ResponseObj.of(statisticsRestResult);
        }
        Map<String, Object> statisticsRestResultBody = statisticsRestResult.getBody();
        DriverInfo driverInfo = driverService.getDriverByUserId(userId);
        if (driverInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED);
        }
        Map<String, Object> driverInfoMap = new HashMap<>();
        UserInfo userInfo = userService.getUserByUserId(userId);
        driverInfoMap.put("avatar", userInfo.getAvatar());
        driverInfoMap.put("name", userInfo.getName().substring(0, 1) + "师傅");
        statisticsRestResultBody.put("driverInfo", driverInfoMap);
        if (UserType.USER_APP_OFFICE == UserType.getEnumsByCode(driverInfo.getType())) {
            List<CarInfo> carInfoList = carService.getCarByUserId(userId);
            if (CollectionUtils.isEmpty(carInfoList)) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED);
            }
            Map<String, Object> carInfoMap = new HashMap<>();
            carInfoMap.put("carPicture", carInfoList.get(0).getCarPicture());
            carInfoMap.put("plateNumber", carInfoList.get(0).getPlateNumber());
            carInfoMap.put("company", carInfoList.get(0).getCompany());
            statisticsRestResultBody.put("carInfo", carInfoMap);
        }
        return ResponseObj.success(statisticsRestResultBody);
    }

    /**
     * 激活账户
     *
     * @param activeAccountForm 表单
     * @param bindingResult     校验结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/activateAccount", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj activateAccount(@RequestBody @Valid ActiveAccountForm activeAccountForm, BindingResult bindingResult) {
        Long userId = getUserId();
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (userInfo == null) {
            redisSetting(userId);//设置用户激活失败次数、时间
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "对不起，您的激活信息与系统内收录信息不符，请确认信息的正确性并重新激活");
        }
        if (UserStatus.getEnumsByCode(userInfo.getStatus()) != UserStatus.UN_ACTIVE) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "用户不是未激活状态");
        }
        Object o = redisHelper.get(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY + userId);
        if (o != null && Integer.parseInt(o.toString()) > 5) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "您今日已连续5次激活失败，系统已锁定您的账户，如需帮助，请联系客服");
        }
        if (bindingResult.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID.getStatus(), bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        Integer userType = getUserType();
        if (!UserType.getEnumsByCode(userType).isAppUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY.getStatus(), StatusCode.NO_AUTHORITY.getDetail());
        }
        if (!userInfo.getName().equals(activeAccountForm.getName())) {
            redisSetting(userId);//设置用户激活重试次数、时间
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "对不起，您的激活信息与系统内收录信息不符，请确认信息的正确性并重新激活");
        }
        DriverInfo driverInfo = driverService.getDriverByUserId(userInfo.getUserId());
        if (driverInfo == null) {
            redisSetting(userId);//设置用户激活重试次数、时间
            redisHelper.incrBy(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY + getUserId(), 1);
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "对不起，您的激活信息与系统内收录信息不符，请确认信息的正确性并重新激活");
        }
        if (!driverInfo.getIdCard().equals(activeAccountForm.getIdCard())) {
            redisSetting(userId);//设置用户激活重试次数、时间
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "对不起，您的激活信息与系统内收录信息不符，请确认信息的正确性并重新激活");
        }
        redisHelper.del(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY + userId);
        redisHelper.set(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId, "2", 3 * 24 * 60 * 60);
        return ResponseObj.success();
    }

    private void redisSetting(Long userId) {
        if (redisHelper.get(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY + userId) == null) {
            redisHelper.set(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY + userId, "1", DateUtils.getSurplusSeconds().intValue());
        } else {
            redisHelper.incrBy(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY + userId, 1);
        }
    }

    /**
     * 激活账户-设置登录密码
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/loginPassword", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj activeLoginPassword(@RequestBody @Valid ModifyPasswordForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (form.getNewPassword().equals(form.getOldPassword())) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "默认密码不能作为新密码");
        }
        Long userId = getUserId();
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (UserStatus.getEnumsByCode(userInfo.getStatus()) != UserStatus.UN_ACTIVE) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "用户不是未激活状态");
        }
        Object object = redisHelper.get(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId);
        if (object == null) {//redis缓存已经过期，但是app那边存的脏数据
            return ResponseObj.fail(StatusCode.NOT_LOGIN);
        }
        if (!object.equals("2")) {
            return ResponseObj.fail(StatusCode.SYS_ERROR);
        }
        ResponseObj responseObj = userService.modifyLoginPassword(userId, form.getOldPassword(), form.getNewPassword());
        if (responseObj.getStatus() == ResponseObj.success().getStatus()) {
            redisHelper.set(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId, "3");
        }
        return responseObj;
    }

    /**
     * 修改密码
     *
     * @param form   表单
     * @param result 校验结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/loginPassword/modify", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj modifyLoginPassword(@RequestBody @Valid ModifyPasswordForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (form.getNewPassword().equals(form.getOldPassword())) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "新旧密码不能相同");
        }
        Long userId = getUserId();
        return userService.modifyLoginPassword(userId, form.getOldPassword(), form.getNewPassword());
    }

    /**
     * 激活账户-设置交易密码
     *
     * @param form 表单
     * @return ResponseObj
     */
    @RequestMapping(value = "/payPassword", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj activePayPassword(@RequestBody UserPatchForm form) {
        if (StringUtils.isBlank(form.getPassword())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, "请输入交易密码");
        }
        Long userId = getUserId();
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (UserStatus.getEnumsByCode(userInfo.getStatus()) != UserStatus.UN_ACTIVE) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "用户不是未激活状态");
        }
        Object object = redisHelper.get(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId);
        if (object == null) {//redis缓存已经过期，但是app那边存的脏数据
            return ResponseObj.fail(StatusCode.NOT_LOGIN);
        }
        if (!object.equals("3")) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED);
        }
        return userService.payPwdSetting(userId, form.getPassword());
    }

    /**
     * 司机身份认证
     *
     * @param form   表单
     * @param result 校验结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/driverIdentity", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj authenticateIdentity(@RequestBody @Valid DriverIdentityForm form, BindingResult result) throws InvocationTargetException, IllegalAccessException {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        Long userId = getUserId();
        DriverInfo driverInfo = driverService.getDriverByUserId(userId);
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (userInfo.getStatus().equals(UserStatus.AUTHENTICATED.getCode())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "已认证，无需再次认证");
        }
        driverInfo.setIsAuthentication(Boolean.TRUE);
        BeanUtilsExtends.copyProperties(driverInfo, form);
        driverService.saveDriverInfo(driverInfo);
        return ResponseObj.success();
    }

    /**
     * 车辆认证
     *
     * @param form   表单
     * @param result 校验
     * @return ResponseObj
     */
    @RequestMapping(value = "/carIdentity", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj authenticateCar(@RequestBody @Valid CarIdentityForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        Long userId = getUserId();
        if (UserType.USER_APP_OFFICE.getCode().equals(getUserType())) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        List<CarInfo> carList = carService.getCarByUserId(userId);
        if (CollectionUtils.isEmpty(carList)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "车辆信息不存在");
        }
        CarInfo carInfo = carList.get(0);
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (UserStatus.AUTHENTICATED.getCode().equals(userInfo.getStatus())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "已认证，无需再次认证");
        }
        carInfo.setIsAuthentication(Boolean.TRUE);
        BeanUtilsExtends.copyProperties(carInfo, form);
        carService.saveCarInfo(carInfo);
        return ResponseObj.success();
    }

    /**
     * 上传头像
     *
     * @param form 请求参数
     * @return ResponseObj
     */
    @RequestMapping(value = "/avatar", method = RequestMethod.PATCH, produces = {"application/json;charset=UTF-8"})
    public ResponseObj uploadAvatar(@RequestBody UserPatchForm form) {
        if (StringUtils.isBlank(form.getAvatar())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, "请上传头像");
        }
        return userService.modifyHeadPicture(getUserId(), form.getAvatar());
    }

    /**
     * 修改交易密码
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/modifyPayPassword", method = RequestMethod.PATCH, produces = {"application/json;charset=UTF-8"})
    public ResponseObj modifyPayPassword(@RequestBody @Valid ModifyPasswordForm form) {
        return userService.modifyPayPwd(getUserId(), form.getOldPassword(), form.getNewPassword());
    }

    /**
     * 重置交易密码
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/resetPayPassword", method = RequestMethod.PATCH, produces = {"application/json;charset=UTF-8"})
    public ResponseObj resetPayPassword(@RequestBody @Valid ModifyBindPhoneForm form) {
        if (StringUtils.equals(form.getPhoneNumber(), getUserName())) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "输入手机号和绑定手机号不符");
        }
        UserType userType = UserType.getEnumsByCode(getUserType());
        return userService.resetPayPwd(form.getPhoneNumber(), form.getPhoneCode(), userType, form.getPassword());
    }

    /**
     * 修改绑定手机号
     *
     * @return ResponseObj
     */
    @RequestMapping(value = "/modifyBindPhone", method = RequestMethod.PATCH, produces = {"application/json;charset=UTF-8"})
    public ResponseObj modifyBindPhone(@RequestBody @Valid ModifyBindPhoneForm form) {
        Long userId = getUserId();
        return userService.modifyUserName(userId, form.getPassword(), form.getPhoneCode(), form.getPhoneNumber());
    }

    /**
     * 新增/编辑常用地址
     *
     * @param form   表单
     * @param result 校验结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/saveAddress", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public ResponseObj saveAddress(@RequestBody @Valid UserAddressForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        UserAddress userAddress = new UserAddress();
        BeanUtilsExtends.copyProperties(userAddress, form);
        userAddress.setUserId(getUserId());
        return userAddressService.saveUserAddress(userAddress);
    }

    /**
     * 用户地址列表
     *
     * @param keywords 搜索关键字
     * @return ResponseObj
     */
    @RequestMapping(value = "/addresses", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public ResponseObj addressList(String keywords) {
        return userAddressService.getUserAddressListByUserId(getUserId(), keywords);
    }

    @RequestMapping(value = "/address/id/{id}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
    public ResponseObj removeAddress(@PathVariable Long id) {
        return userAddressService.removeUserAddress(id);
    }
}
