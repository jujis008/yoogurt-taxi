package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "finance_wx_settings")
public class FinanceWxSettings extends SuperModel {

    @Id
    @Column(name = "app_id")
    private String appId;

    /**
     * 微信创建的应用ID
     */
    @Column(name = "wx_app_id")
    private String wxAppId;

    /**
     * 微信开放平台 - 管理中心 - 移动应用 - 已获得微信支付能力的应用 - 查看 - AppSecret
     */
    @Column(name = "app_secret")
    private String appSecret;

    /**
     * 商户ID
     */
    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "api_secret")
    private String apiSecret;

    /**
     * 员工账号
     */
    @Column(name = "employee_id")
    private String employeeId;

    /**
     * 微信支付商户平台 - 账户中心 - 账户设置 - API 安全 - API 证书 - 下载证书
     */
    @Column(name = "api_certificate")
    private String apiCertificate;

    /**
     * 微信支付商户平台 - 账户中心 - 账户设置 - API 安全 - API 证书 - 下载证书
     */
    @Column(name = "private_key")
    private String privateKey;

}