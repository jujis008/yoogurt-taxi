package com.yoogurt.taxi.notification.config;

import com.yoogurt.taxi.notification.bo.Transmission;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "push.end20")
public class GeTuiConfig {

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
     * 个推申请的安全密钥master_secret
     */
    @Value("master")
    private String master;

    /**
     * 通知的标题
     */
    private String title;

    /**
     * 推送的内容
     */
    private String content;

    /**
     * 透传消息
     */
    private Transmission transmission;

}
