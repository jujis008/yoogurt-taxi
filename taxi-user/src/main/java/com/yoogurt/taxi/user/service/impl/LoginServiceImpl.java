package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserFrom;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.user.dao.UserDao;
import com.yoogurt.taxi.user.service.AuthorityInfoService;
import com.yoogurt.taxi.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthorityInfoService authorityInfoService;


    @Override
    public ResponseObj login(String username, String password, UserType userType) {
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username)
                .andEqualTo("isDeleted", 0);
        if (userType.isAppUser()) {
            criteria.andEqualTo("type", userType.getCode());
        }
        List<UserInfo> userList = userDao.selectByExample(example);
        if (userList.size() == 0) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "登录失败，请核对账号");
        }
        UserInfo user = userList.get(0);
        // 密码不匹配
        if (!Encipher.matches(password, user.getLoginPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "登录失败，请核对密码");
        }
        if (user.getStatus().equals(UserStatus.FROZEN.getCode())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"账号已被冻结");
        }
        //生成授权码
        String grantCode = RandomUtils.getRandNum(Constants.GRANT_CODE_LENGTH);
        SessionUser sessionUser = new SessionUser(user.getUserId(), username);
        sessionUser.setStatus(user.getStatus());
        sessionUser.setGrantCode(grantCode);
        sessionUser.setType(user.getType());
        //缓存授权码，30秒内有效
        redisHelper.set(CacheKey.GRANT_CODE_KEY + user.getUserId(), grantCode, Constants.GRANT_CODE_EXPIRE_SECONDS);
        //缓存SessionUser，不需要设置过期时间，以JWT的过期时间为准
        redisHelper.setObject(CacheKey.SESSION_USER_KEY + user.getUserId(), sessionUser);
        ResponseObj success = ResponseObj.success(sessionUser);
        if (userType.isWebUser()) {
            List<String> tagList = authorityInfoService.getAssociatedControlByUserId(user.getUserId());
            HashMap<String, Object> extras = new HashMap<>();
            extras.put("tagList",tagList);
            success.setExtras(extras);
        }
        return success;
    }

    @Override
    public ResponseObj register(String username, String password, UserType userType) {
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username).andEqualTo("type", userType.getCode());
        List<UserInfo> userList = userDao.selectByExample(example);
        if(userList.size()!=0) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(),"账号已存在");
        }
        String userId = RandomUtils.getPrimaryKey();
        UserInfo user = new UserInfo();
        user.setUserId(userId);
        user.setUsername(username);
        user.setLoginPassword(Encipher.encrypt(password));
        user.setType(userType.getCode());
        user.setStatus(UserStatus.UN_AUTHENTICATE.getCode());
        user.setUserFrom(UserFrom.APP.getCode());
        user.setIsDeleted(Boolean.FALSE);
        userDao.insert(user);
        return ResponseObj.success();
    }

}
