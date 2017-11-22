package com.yoogurt.taxi.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {
    @Value("version")
    private String version;
    @Value("restServer")
    private String restServer;
    @Value("accountSid")
    private String accountSid;
    @Value("authToken")
    private String authToken;
    @Value("RestUrl")
    private String restUrl;
    @Value("appId")
    private String appId;
    @Value("validTemplateId")
    private String validTemplateId;
    @Value("agentPwdTemplateId")
    private String agentPwdTemplateId;
    @Value("officePwdTemplateId")
    private String officePwdTemplateId;
    @Value("officeResetPwdTemplateId")
    private String officeResetPwdTemplateId;
    @Value("agentResetPwdTemplateId")
    private String agentResetPwdTemplateId;
}
