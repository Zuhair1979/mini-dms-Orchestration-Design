package com.orchestration.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix="es")
public record ClientConfig(String baseUrl, String index) {
}
