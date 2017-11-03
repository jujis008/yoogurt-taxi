package com.yoogurt.taxi.system;

import com.yoogurt.taxi.common.helper.RedisHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaxiSystemApplicationTests {
	@Autowired
	private RedisHelper redisHelper;

	@Test
    @Ignore
	public void contextLoads() {
	    redisHelper.getConnectionFactory().getConnection().select(1);
	    redisHelper.set("task1","this is a task",10000);
	}

	public void sub() {
	    String pattern = "__key*__:*";
    }

}
