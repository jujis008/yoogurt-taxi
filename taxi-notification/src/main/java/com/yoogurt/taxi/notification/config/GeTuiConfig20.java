package com.yoogurt.taxi.notification.config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration("config20")
@ConfigurationProperties(prefix = "push.end20")
public class GeTuiConfig20 implements IGeTuiConfig {

    /**
     * 个推接口地址
     */
    @Value("host")
    private String host;
    /**
     * 个推申请的应用ID
     */
    @Value("appId")
    private String appId;
    /**
     * 个推申请的应用KEY
     */
    @Value("appKey")
    private String appKey;

    /**
     * 第三方客户端个推集成鉴权码，用于验证第三方合法性。
     * 在客户端集成SDK时需要提供。
     */
    @Value("appSecret")
    private String appSecret;

    /**
     * 个推申请的安全密钥master_secret
     */
    @Value("masterSecret")
    private String masterSecret;

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public String getAppKey() {
        return this.appKey;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public String getAppSecret() {
        return this.appSecret;
    }

    @Override
    public String getMasterSecret() {
        return this.masterSecret;
    }
}
