package com.yoogurt.taxi.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "yoogurt.jwt")
public class JwtProperties {

    private String header;

    private String basic;

    private String secret;

    private Integer expireSeconds;
}