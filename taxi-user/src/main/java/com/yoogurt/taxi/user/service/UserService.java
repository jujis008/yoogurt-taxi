package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.pager.WebPager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.user.UserWLModel;

import java.util.List;

public interface UserService {

    /**
     * 根据用户主键id获取用户信息
     * @param id
     * @return
     */
    UserInfo getUserByUserId(Integer id);

    /**
     * 修改登陆密码
     * @param userId        用户主键id
     * @param oldPassword   旧密码
     * @param newPassword   新密码
     * @return
     */
    ResponseObj modifyLoginPassword(Long userId, String oldPassword, String newPassword);

    /**
     * 修改头像
     * @param userId    用户主键id
     * @param avatar    头像url
     * @return
     */
    ResponseObj modifyHeadPicture(Long userId, String avatar);

    /**
     * app端重置登陆密码
     * @param username  用户名
     * @param phoneCode 短信验证码
     * @param userType  用户类型
     * @return
     */
    ResponseObj resetLoginPwd(String username, String phoneCode, UserType userType, String newPassword);

    /**
     * web端重置登陆密码
     * @param userId    用户主键id
     * @param password  密码
     * @return
     */
    ResponseObj resetLoginPwd(Long userId, String password);

    /**
     * 设置交易密码
     * @param userId        用户主键id
     * @param payPassword   交易密码
     * @return
     */
    ResponseObj payPwdSetting(Long userId, String payPassword);

    /**
     * 修改交易密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    ResponseObj modifyPayPwd(Long userId, String oldPassword, String newPassword);

    /**
     * 重置交易密码
     * @param username
     * @param phoneCode
     * @param userType
     * @return
     */
    ResponseObj resetPayPwd(String username, String phoneCode, UserType userType, String password);

    /**
     * 修改绑定手机号
     * @param userId
     * @param password
     * @param phoneCode
     * @param phoneNumber
     * @return
     */
    ResponseObj modifyUserName(Long userId, String password, String phoneCode, String phoneNumber);

    /**
     * 修改用户状态
     * @param userId
     * @param userStatus
     */
    void modifyUserStatus(Long userId, UserStatus userStatus);

    /**
     * 删除用户（软删除）
     * @param userId
     * @return
     */
    ResponseObj removeUser(Long userId);

    /**
     * 根据条件，分页查询用户数据（web）
     * @param condition
     * @return
     */
    Pager<UserInfo> getUserWebList(UserWLCondition condition);
}
