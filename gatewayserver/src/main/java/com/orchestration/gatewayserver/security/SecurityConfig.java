package com.orchestration.gatewayserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig  {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        var jwtConvertor=new ReactiveJwtAuthenticationConverterAdapter(KeycloakJwtConvertor.customConvertor());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)     // <-- turn off CSRF for API gateway
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.GET,"/actuator").permitAll()
                        .pathMatchers(HttpMethod.GET,"/**").hasAnyRole("admin","viewer","uploader")
                        .pathMatchers(HttpMethod.DELETE,"/**").hasRole("admin")
                        .pathMatchers(HttpMethod.PUT,"/**").hasRole("admin")
                        .pathMatchers(HttpMethod.POST,"/**").hasRole("admin")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConvertor)))
                .build();
    }


}