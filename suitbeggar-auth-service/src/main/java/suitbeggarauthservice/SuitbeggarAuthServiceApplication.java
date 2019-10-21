package suitbeggarauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 微服务之间直接调用的认证中心
 */
@SpringBootApplication
@EnableEurekaClient
public class SuitbeggarAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuitbeggarAuthServiceApplication.class, args);
	}

}
