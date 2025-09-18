package com.orchestration.storage.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageCredentials.class)
public class MinioClientConfig {

    @Bean
   public MinioClient getClient(StorageCredentials cre){
        return MinioClient.builder()
                .endpoint(cre.endpoint())
                .credentials(cre.accessKey(), cre.secretKey())
                .build();
    }

    // confirm the buckt is exist or create new on
    @Bean
    public CommandLineRunner bucktIsExist(MinioClient client, StorageCredentials cre){
    return cLr ->{
      boolean isExist=client.bucketExists(
              BucketExistsArgs.builder().bucket("dms-files").build()
      );
      if(!isExist){
          client.makeBucket(MakeBucketArgs.builder().bucket(cre.bucket()).build());
      }

    };

    }
}
