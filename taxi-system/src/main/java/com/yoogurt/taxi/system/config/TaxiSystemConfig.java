package com.yoogurt.taxi.system.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.yoogurt.taxi")
@Import({OssConfig.class})
public class TaxiSystemConfig {
}
