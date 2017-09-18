package com.yoogurt.taxi.gateway;

import com.yoogurt.taxi.common.helper.TokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaxiGatewayBootstrap.class)
public class TaxiGatewayApplicationTests {

	@Autowired
	private TokenHelper tokenHelper;

	@Test
	public void createTokenTest() {
		String token = tokenHelper.createToken(17091410051320L, "18814892833");
		Assert.assertNotNull("token is null", token);
		log.info("获得的token：" + token);
	}

	@Test
	public void getUserIdTest() {

		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxNzA5MTQxMDA1MTMyMzMwIiwic3ViIjoiMTg4MTQ4OTI4MzMiLCJpc3MiOiJ5b29ndXJ0LnRheGkuZ2F0ZXdheSIsImlhdCI6MTUwNTM1NjUyMiwiZXhwIjoxNTA1MzYyNTIyfQ.Te4Huo8Uvj9Y-ELhwfHfyivo88ewSMS5zPM-TscZcIUGiZOL7iGTNgkY6zIeFUwJ8RywdvxNMGxiNeQf5TpsKQ";
		Long userId = tokenHelper.getUserId(token);
		Assert.assertEquals("1709141005132330", userId);
		log.info("获得的userId：" + userId);
	}

	@Test
	public void getUserNameTest() {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxNzA5MTQxMDA1MTMyMzMwIiwic3ViIjoiMTg4MTQ4OTI4MzMiLCJpc3MiOiJ5b29ndXJ0LnRheGkuZ2F0ZXdheSIsImlhdCI6MTUwNTM1NjUyMiwiZXhwIjoxNTA1MzYyNTIyfQ.Te4Huo8Uvj9Y-ELhwfHfyivo88ewSMS5zPM-TscZcIUGiZOL7iGTNgkY6zIeFUwJ8RywdvxNMGxiNeQf5TpsKQ";
		String username = tokenHelper.getUserName(token);
		Assert.assertEquals("18814892833", username);
		log.info("获得的username：" + username);
	}

	@Test
	public void getCreatedDateTest() {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxNzA5MTQxMDA1MTMyMzMwIiwic3ViIjoiMTg4MTQ4OTI4MzMiLCJpc3MiOiJ5b29ndXJ0LnRheGkuZ2F0ZXdheSIsImlhdCI6MTUwNTM1NjUyMiwiZXhwIjoxNTA1MzYyNTIyfQ.Te4Huo8Uvj9Y-ELhwfHfyivo88ewSMS5zPM-TscZcIUGiZOL7iGTNgkY6zIeFUwJ8RywdvxNMGxiNeQf5TpsKQ";
		Date createdDate = tokenHelper.getCreatedDate(token);
		Assert.assertNotNull("创建日期获取失败", createdDate);
		log.info("创建日期：" + createdDate);
	}

	@Test
	public void isTokenExpiredTest() {
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxNzA5MTQxMDA1MTMyMzMwIiwic3ViIjoiMTg4MTQ4OTI4MzMiLCJpc3MiOiJ5b29ndXJ0LnRheGkuZ2F0ZXdheSIsImlhdCI6MTUwNTM1NjUyMiwiZXhwIjoxNTA1MzYyNTIyfQ.Te4Huo8Uvj9Y-ELhwfHfyivo88ewSMS5zPM-TscZcIUGiZOL7iGTNgkY6zIeFUwJ8RywdvxNMGxiNeQf5TpsKQ";
		Boolean expired = tokenHelper.isTokenExpired(token);
		Assert.assertEquals(expired, tokenHelper.remainTimes(token) <= 0);
		log.info(expired ? "已过期" : "未过期");
	}
}
