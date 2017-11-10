package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import com.yoogurt.taxi.dal.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
@Domain
@Setter
@Getter
@Table(name = "user_info")
public class UserInfo extends SuperModel {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * 工号
     */
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

}