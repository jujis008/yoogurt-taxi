package com.yoogurt.taxi.finance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@EnableFeignClients
@EnableEurekaClient
@EnableHystrix
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan(basePackages = {"com.yoogurt.taxi"})
public class TaxiFinanceBootstrap {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(TaxiFinanceBootstrap.class, args);
		ConfigurableEnvironment env = context.getEnvironment();
		log.info("\n----------------------------------------------------------\n\t" +
						"Application '{}' is running! Access URLs:\n\t" +
						"Local: \t\thttp://127.0.0.1:{}\n\t" +
						"External: \thttp://{}:{}\n\t" +
						"----------------------------------------------------------",

				env.getProperty("spring.application.name"),
				env.getProperty("server.port"),
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"));
	}

	/**
	 * 构造一个RestTemplate。
	 * StringHttpMessageConverter的默认编码集是ISO8859-1，需要改成UTF-8
	 * @return RestTemplate
	 */
	@Bean
	public RestTemplate getRestTemplate() {

		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		//StringHttpMessageConverter追加在第二位，先移除之
		messageConverters.remove(1);
		//构造一个新的StringHttpMessageConverter，改变默认编码
		StringHttpMessageConverter converter = new StringHttpMessageConverter();
		converter.setDefaultCharset(Charset.forName("UTF-8"));
		messageConverters.add(converter);
		//重新设置converters
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;

	}
}
