package com.yoogurt.taxi.notification.config;

import com.yoogurt.taxi.notification.config.getui.GeTuiConfig20;
import com.yoogurt.taxi.notification.config.getui.GeTuiConfig30;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.yoogurt.taxi")
@Import({SmsConfig.class, GeTuiConfig20.class, GeTuiConfig30.class})
public class TaxiNotificationConfig {
}
