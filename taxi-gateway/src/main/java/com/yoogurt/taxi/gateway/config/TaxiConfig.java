package com.yoogurt.taxi.gateway.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({"com.yoogurt.taxi"})
@Import({
        JwtConfig.class,
        ShiroConfig.class
})
public class TaxiConfig {
}
