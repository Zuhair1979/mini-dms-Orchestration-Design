package com.orchestration.gatewayserver.routes;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;
import java.util.Set;

@Configuration
public class GateWayRoutes {

    @Bean
    RouteLocator dmsRoutes(RouteLocatorBuilder r, RedisRateLimiter redisRateLimiter,
                           KeyResolver principalOrIpKeyResolver) {
        return r.routes()
                .route(
                        p -> p
                                .path("/mini-dms-demo/meta/**")
                                .filters(f -> f
                                        .rewritePath("/mini-dms-demo/(?<segment>.*)", "/${segment}")
                                        .retry(retryConfig -> retryConfig
                                                .setRetries(2).setMethods(HttpMethod.GET)
                                                .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.BAD_GATEWAY,
                                                        HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.GATEWAY_TIMEOUT)
                                                .setExceptions(java.util.concurrent.TimeoutException.class,
                                                        java.net.ConnectException.class)
                                                .setBackoff(Duration.ofMillis(200),Duration.ofMillis(1000),2,true)
                                        )
                                        .circuitBreaker(c -> c
                                                .setName("metadataCB")
                                                .setFallbackUri("forward:/fallback/meta")

                                        )
                                        .requestRateLimiter(rl -> rl
                                                .setRateLimiter(redisRateLimiter)
                                                .setKeyResolver(principalOrIpKeyResolver)
                                        )
                                )

                                .uri("lb://metadata")
                )
                .route(p -> p
                        .path("/mini-dms-demo/storage/**")
                        .filters(x -> x
                                .rewritePath("/mini-dms-demo/(?<seg>.*)", "/${seg}")
                                .retry(retryConfig -> retryConfig
                                        .setRetries(1).setMethods(HttpMethod.GET)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.BAD_GATEWAY,
                                                HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.GATEWAY_TIMEOUT)// only do retry when we have
                                        // server error 5XX, not 4XX as it is for client issue
                                        .setExceptions(java.util.concurrent.TimeoutException.class,
                                                java.net.ConnectException.class) // also retry for these exceptions
                                        .setBackoff(Duration.ofMillis(200),Duration.ofMillis(1000)
                                        ,2,true))
                                .circuitBreaker(c -> c .setName("storageCB")
                                        .setFallbackUri("forward:/fallback/storage")
                                        )
                                .requestRateLimiter(rl -> rl
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(principalOrIpKeyResolver)
                                )
                        )
                        .uri("lb://storage")
                )
                .route(p -> p
                        .path("/mini-dms-demo/search/**")
                        .filters(c ->
                                c.rewritePath("/mini-dms-demo/(?<seg>.*)", "/${seg}")
                                        .retry(retryConfig -> retryConfig
                                                .setRetries(2)
                                                .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.BAD_GATEWAY,
                                                        HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.GATEWAY_TIMEOUT)
                                                .setExceptions(java.util.concurrent.TimeoutException.class,
                                                        java.net.ConnectException.class)
                                                .setMethods(HttpMethod.GET).setBackoff(Duration.ofMillis(200),
                                                        Duration.ofMillis(1000),2,true))
                                        .circuitBreaker(config ->config
                                                .setName("searchCB").setFallbackUri("forward:/fallback/search")
                                                )
                                        .requestRateLimiter(rl -> rl
                                                .setRateLimiter(redisRateLimiter)
                                                .setKeyResolver(principalOrIpKeyResolver)
                                        )
                        )
                        .uri("lb://search"))

                .build();


    }

    @Bean
    public KeyResolver principalOrIpKeyResolver() {
        // Prefer authenticated user (Principal); otherwise use client IP; otherwise "unknown"
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)  // e.g., JWT subject/username
                .switchIfEmpty(Mono.justOrEmpty(
                        exchange.getRequest().getRemoteAddress() != null
                                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                                : "unknown"));
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }
}


/*

 return r.routes()
                .route("metadata-health", spec -> spec
        .path("/dms/metadata/actuator/**")   // predicate
                        .filters(f -> f.rewritePath("/dms/metadata/(?<segment>.*)", "/${segment}"))      // StripPrefix=2
        .uri("lb://metadata"))               // load-balanced via Eureka
        .build();*/
