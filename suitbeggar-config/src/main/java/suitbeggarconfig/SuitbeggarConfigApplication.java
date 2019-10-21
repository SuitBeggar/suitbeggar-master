package suitbeggarconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigServer
public class SuitbeggarConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuitbeggarConfigApplication.class, args);
	}

}
