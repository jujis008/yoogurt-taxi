package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "user_info")
public class UserInfo extends SuperModel{
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 工号
     */
    @Id
    @Column(name = "employee_no")
    private String employeeNo;

    /**
     * 用户名，登陆账号，也作为联系方式
     */
    private String username;

    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 密码
     */
    @Column(name = "login_password")
    private String loginPassword;

    @Column(name = "pay_password")
    private String payPassword;

    /**
     * 用户来源：10-导入，20-APP注册，30-后台添加
     */
    @Column(name = "user_from")
    private Integer userFrom;

    /**
     * 用户类型:0-（super_admin）超级管理员，10（USER_WEB）-后端用户,20（USER_APP_AGENT）-代理端用户,30（USER_APP_OFFICE）-正式端用户
     */
    private Integer type;

    /**
     * 账号状态:10（UN_ACTIVE）-未激活，20（UN_AUTHENTICATE）-未认证，30（UNTHENTICATED）-已认证，40（FROZEN）-冻结
     */
    private Integer status;


    /**
     * @return user_id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取工号
     *
     * @return employee_no - 工号
     */
    public String getEmployeeNo() {
        return employeeNo;
    }

    /**
     * 设置工号
     *
     * @param employeeNo 工号
     */
    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    /**
     * 获取用户名，登陆账号，也作为联系方式
     *
     * @return username - 用户名，登陆账号，也作为联系方式
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名，登陆账号，也作为联系方式
     *
     * @param username 用户名，登陆账号，也作为联系方式
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取头像
     *
     * @return avatar - 头像
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像
     *
     * @param avatar 头像
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取密码
     *
     * @return login_password - 密码
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * 设置密码
     *
     * @param loginPassword 密码
     */
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    /**
     * @return pay_password
     */
    public String getPayPassword() {
        return payPassword;
    }

    /**
     * @param payPassword
     */
    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    /**
     * 获取用户来源：10-导入，20-APP注册，30-后台添加
     *
     * @return user_from - 用户来源：10-导入，20-APP注册，30-后台添加
     */
    public Integer getUserFrom() {
        return userFrom;
    }

    /**
     * 设置用户来源：10-导入，20-APP注册，30-后台添加
     *
     * @param userFrom 用户来源：10-导入，20-APP注册，30-后台添加
     */
    public void setUserFrom(Integer userFrom) {
        this.userFrom = userFrom;
    }

    /**
     * 获取用户类型:0-（super_admin）超级管理员，10（USER_WEB）-后端用户,20（USER_APP_AGENT）-代理端用户,30（USER_APP_OFFICE）-正式端用户
     *
     * @return type - 用户类型:0-（super_admin）超级管理员，10（USER_WEB）-后端用户,20（USER_APP_AGENT）-代理端用户,30（USER_APP_OFFICE）-正式端用户
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置用户类型:0-（super_admin）超级管理员，10（USER_WEB）-后端用户,20（USER_APP_AGENT）-代理端用户,30（USER_APP_OFFICE）-正式端用户
     *
     * @param type 用户类型:0-（super_admin）超级管理员，10（USER_WEB）-后端用户,20（USER_APP_AGENT）-代理端用户,30（USER_APP_OFFICE）-正式端用户
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取账号状态:10（UN_ACTIVE）-未激活，20（UN_AUTHENTICATE）-未认证，30（UNTHENTICATED）-已认证，40（FROZEN）-冻结
     *
     * @return status - 账号状态:10（UN_ACTIVE）-未激活，20（UN_AUTHENTICATE）-未认证，30（UNTHENTICATED）-已认证，40（FROZEN）-冻结
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置账号状态:10（UN_ACTIVE）-未激活，20（UN_AUTHENTICATE）-未认证，30（UNTHENTICATED）-已认证，40（FROZEN）-冻结
     *
     * @param status 账号状态:10（UN_ACTIVE）-未激活，20（UN_AUTHENTICATE）-未认证，30（UNTHENTICATED）-已认证，40（FROZEN）-冻结
     */
    public void setStatus(Integer status) {
        this.status = status;
    }


}