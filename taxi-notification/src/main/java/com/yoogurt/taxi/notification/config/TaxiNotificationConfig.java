package com.yoogurt.taxi.notification.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.yoogurt.taxi")
@Import({SmsConfig.class})
public class TaxiNotificationConfig {
}
