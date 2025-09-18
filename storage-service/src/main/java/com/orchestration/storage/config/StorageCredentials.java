package com.orchestration.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.minio")
public record StorageCredentials(String endpoint, String accessKey, String secretKey, String bucket) {
}
