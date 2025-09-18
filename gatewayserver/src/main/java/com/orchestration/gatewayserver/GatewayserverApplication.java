package com.orchestration.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}
/*
	@Bean
	RouteLocator dmsRoutes(RouteLocatorBuilder r){
		return r.routes()
				.route("metadata-health", spec -> spec
						.path("/dms/metadata/actuator/**")   // predicate
						.filters(f -> f.rewritePath("/dms/metadata/(?<segment>.*)", "/${segment}"))      // StripPrefix=2
						.uri("lb://metadata"))               // load-balanced via Eureka
				.build();

	}*/
}
