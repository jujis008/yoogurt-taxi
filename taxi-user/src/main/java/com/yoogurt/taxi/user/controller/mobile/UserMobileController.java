package com.yoogurt.taxi.user.controller.mobile;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.DateUtil;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.user.Form.ActiveAccountForm;
import com.yoogurt.taxi.user.Form.LoginForm;
import com.yoogurt.taxi.user.service.DriverService;
import com.yoogurt.taxi.user.service.LoginService;
import com.yoogurt.taxi.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/mobile/user")
public class UserMobileController extends BaseController{

    @Autowired
    private LoginService    loginService;
    @Autowired
    private UserService     userService;
    @Autowired
    private DriverService   driverService;
    @Autowired
    private RedisHelper     redisHelper;

    @RequestMapping(value = "/i/login", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj login(@RequestBody LoginForm loginForm) {
        if (StringUtils.isBlank(loginForm.getUsername())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK.getStatus(),"请填写用户名");
        }
        if (StringUtils.isBlank(loginForm.getPassword())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK.getStatus(),"请填写密码");
        }
        return loginService.login(loginForm.getUsername(),loginForm.getPassword(), UserType.USER_WEB);
    }

    @RequestMapping(value = "/activateAccount", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj activateAccount(@Valid ActiveAccountForm activeAccountForm, BindingResult bindingResult) {
        Long userId = getUserId();
        Object o = redisHelper.get(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY + userId);
        if(o!=null && Integer.parseInt(o.toString())>5){
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"对不起，您的激活信息与系统内收录信息不符，请确认信息的正确性并重新激活");
        }
        if(bindingResult.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID.getStatus(),bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if(UserType.getEnumsByCode(getUserType()).isAppUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY.getStatus(),StatusCode.NO_AUTHORITY.getDetail());
        }
        UserInfo userInfo = userService.getUserByUserId(userId);
        if(userInfo == null) {
            redisSetting(userId);//设置用户激活失败次数、时间
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"信息匹配有误");
        }
        if (!userInfo.getName().equals(activeAccountForm.getName())) {
            redisSetting(userId);//设置用户激活重试次数、时间
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"信息匹配有误");
        }
        DriverInfo driverInfo = driverService.getDriverByUserId(userInfo.getUserId());
        if (driverInfo == null) {
            redisSetting(userId);//设置用户激活重试次数、时间
            redisHelper.incrBy(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY+getUserId(),1);
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"信息匹配有误");
        }
        if (!driverInfo.getIdCard().equals(activeAccountForm.getIdCard())) {
            redisSetting(userId);//设置用户激活重试次数、时间
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"信息匹配有误");
        }
        redisHelper.del(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY+userId);
        redisHelper.set(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY+userId,1,3*24*60*60);
        return ResponseObj.success();
    }

    public void redisSetting(Long userId){
        if (redisHelper.get(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY+userId) == null){
            redisHelper.set(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY+userId, 1, DateUtil.getSurplusSeconds().intValue());
        } else {
            redisHelper.incrBy(CacheKey.ACTIVATE_RETRY_MAX_COUNT_KEY+userId,1);
        }
    }

    @RequestMapping(value = "/activeLoginPassword", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj activeLoginPassword(String oldPassword,String newPassword) {
        if (StringUtils.isBlank(oldPassword)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"请输入默认密码");
        }
        if (StringUtils.isBlank(newPassword)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"请输入新密码");
        }
        if (oldPassword.equals(newPassword)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"默认密码不能作为新密码");
        }
        Long userId = getUserId();
        Object object = redisHelper.get(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId);
        if (object == null) {//redis缓存已经过期，但是app那边存的脏数据
            return ResponseObj.fail(StatusCode.NOT_LOGIN);
        }
        if (!object.equals(1)) {
            return ResponseObj.fail(StatusCode.SYS_ERROR);
        }
        ResponseObj responseObj = userService.modifyLoginPassword(userId, oldPassword, newPassword);
        if (responseObj.getStatus()==ResponseObj.success().getStatus()) {
            redisHelper.set(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId,2);
        }
        return responseObj;
    }

    @RequestMapping(value = "/activePayPassword", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj activePayPassword(String password) {
        if (StringUtils.isBlank(password)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"请输入交易密码");
        }
        Long userId = getUserId();
        Object object = redisHelper.get(CacheKey.ACTIVATE_PROGRESS_STATUS_KEY + userId);
        if (object == null) {//redis缓存已经过期，但是app那边存的脏数据
            return ResponseObj.fail(StatusCode.NOT_LOGIN);
        }
        if (!object.equals(2)) {
            return ResponseObj.fail(StatusCode.SYS_ERROR);
        }
        ResponseObj responseObj = userService.payPwdSetting(userId, password);
        return responseObj;
    }

}
