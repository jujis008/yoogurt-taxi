package com.yoogurt.taxi.system.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OssTokenModel {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String expiration;
    private String arn;
    private String assumedRoleId;
}
