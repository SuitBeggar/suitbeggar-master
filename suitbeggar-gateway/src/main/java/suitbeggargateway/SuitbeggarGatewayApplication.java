package suitbeggargateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
public class SuitbeggarGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuitbeggarGatewayApplication.class, args);
	}

	/**
	 * 路由配置  两种方式，一种配置文件，一种代码（当前）
	 * @param builder
	 * @return
	 */
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("path_route",r -> r.path("/hj").uri("https://www.baidu.com"))
				.build();
	}

	/**
	 * 用户限流
	 * @return
	 */
	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("userId"));
	}

	/**
	 * 接口限流
	 * @return
	 */
	@Bean
	KeyResolver apiKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getPath().value());
	}

	/**
	 *IP限流
	 * @return
	 */

	@Bean
	@Primary
	KeyResolver ipKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
	}


	//@Bean
	//@Primary
    //使用自己定义的限流类  （几种限流方式使用一种）
/*	SystemRedisRateLimiter systemRedisRateLimiter(
			ReactiveRedisTemplate<String, String> redisTemplate,
			@Qualifier(SystemRedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> script,
			Validator validator){
		return new SystemRedisRateLimiter(redisTemplate , script , validator);
	}*/
}
