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
@Table(name = "finance_alipay_settings")
public class FinanceAlipaySettings extends SuperModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 合作伙伴身份ID，支付宝开放平台 - PID 和公钥管理 - mapi 网关产品密钥 - 合作伙伴身份（PID）
     */
    private String pid;

    /**
     * 支付宝企业账户（邮箱）
     */
    @Column(name = "seller_id")
    private String sellerId;

    /**
     * 支付应用ID，支付应用ID<支付宝开放平台 - PID 和公钥管理 - 开放平台密钥 - APPID支付宝开放平台 - PID 和公钥管理 - 开放平台密钥 - APPID
     */
    @Column(name = "alipay_app_id")
    private String alipayAppId;

    /**
     * MD5 密钥，支付宝开放平台 - PID 和公钥管理 - mapi 网关产品密钥 - 合作伙伴密钥 - 安全校验码（key） - MD5 密钥
     */
    @Column(name = "md5_secret")
    private String md5Secret;

    /**
     * 加密方式，默认RSA2
     */
    @Column(name = "sign_type")
    private String signType;

    /**
     * 支付宝公钥
     */
    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "private_key")
    private String privateKey;

}