package com.yoogurt.taxi.notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackages = "com.yoogurt.taxi")
public class TaxiNotificationApplicationTests {
	@Test
	public void contextLoads() throws Exception {
		String accountSid = "";
		String authToken = "";
		String appId = "";
		String templateId = "";
		String to = "";
		String param = "";
		System.out.println("");
	}

}
