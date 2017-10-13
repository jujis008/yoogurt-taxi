package com.yoogurt.taxi.system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Getter
@Setter
@ConfigurationProperties(prefix = "oss.appUser")
public class OssConfig {
    @Value("accessKeyId")
    private String accessKeyId;
    @Value("accessKeySecret")
    private String accessKeySecret;
    @Value("roleArn")
    private String roleArn;
}
