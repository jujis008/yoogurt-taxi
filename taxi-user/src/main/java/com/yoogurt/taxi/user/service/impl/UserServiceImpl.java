package com.yoogurt.taxi.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.user.UserWLModel;
import com.yoogurt.taxi.user.dao.UserDao;
import com.yoogurt.taxi.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PagerFactory webPagerFactory;

    @Override
    public UserInfo getUserByUserId(Long id) {
        return userDao.selectById(id);
    }

    @Override
    public ResponseObj modifyLoginPassword(Long userId, String oldPassword, String newPassword) {
        UserInfo user = userDao.selectById(userId);
        if(user == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"用户不存在");
        }
        if(Encipher.matches(oldPassword, user.getLoginPassword())) {
            user.setLoginPassword(Encipher.encrypt(newPassword));
            userDao.updateById(user);
            return ResponseObj.success();
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"旧密码错误");
    }

    @Override
    public ResponseObj modifyHeadPicture(Long userId, String avatar) {
        UserInfo user = userDao.selectById(userId);
        if(user == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"用户不存在");
        }
        user.setAvatar(avatar);
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj resetLoginPwd(String username, String phoneCode, UserType userType, String newPassword) {
        Object cachePhoneCode = redisHelper.get(CacheKey.VERIFY_CODE_KEY+username);
        if(cachePhoneCode == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"验证码过期，请重新获取");
        }
        if(!cachePhoneCode.equals(phoneCode)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"验证码错误");
        }
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("username",username);
        example.createCriteria().andEqualTo("type",userType.getCode());
        List<UserInfo> userList = userDao.selectByExample(example);
        if(userList.size() == 0) {
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
        user.setPayPassword(Encipher.encrypt(payPassword));
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj modifyPayPwd(Long userId, String oldPassword, String newPassword) {
        UserInfo user = userDao.selectById(userId);
        if(Encipher.matches(oldPassword, user.getPayPassword())) {
            user.setPayPassword(Encipher.encrypt(newPassword));
            userDao.updateById(user);
            return ResponseObj.success();
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"旧密码错误");
    }

    @Override
    public ResponseObj resetPayPwd(String username, String phoneCode, UserType userType, String password) {
        Object cachePhoneCode = redisHelper.get(CacheKey.VERIFY_CODE_KEY+username);
        if(cachePhoneCode == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"验证码过期，请重新获取");
        }
        if(!cachePhoneCode.equals(phoneCode)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"验证码错误");
        }
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("username",username);
        example.createCriteria().andEqualTo("type",userType.getCode());
        List<UserInfo> userList = userDao.selectByExample(example);
        if(userList.size() == 0) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "账号有误");
        }
        UserInfo user = userList.get(0);
        user.setPayPassword(Encipher.encrypt(password));
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj modifyUserName(Long userId, String password, String phoneCode, String phoneNumber) {
        UserInfo user = userDao.selectById(userId);
        if (user == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"账号异常");
        }
        if(!Encipher.matches(password, user.getLoginPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"密码有误");
        }
        Object cachePhoneCode = redisHelper.get(CacheKey.VERIFY_CODE_KEY+phoneNumber);
        if(cachePhoneCode == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"验证码过期，请重新获取");
        }
        if(!cachePhoneCode.equals(phoneCode)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"验证码错误");
        }
        user.setUsername(phoneNumber);
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
        user.setIsDeleted(Boolean.TRUE);
        userDao.updateById(user);
        return ResponseObj.success();
    }

    @Override
    public Pager<UserWLModel> getUserWebList(UserWLCondition condition) {
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
        Page<UserWLModel> userWebListPage = userDao.getUserWebListPage(condition);
        return webPagerFactory.generatePager(userWebListPage);
    }
}
