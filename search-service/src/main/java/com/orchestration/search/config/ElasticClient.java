package com.orchestration.search.config;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@EnableConfigurationProperties(ClientConfig.class)
@Configuration
@RequiredArgsConstructor
public class ElasticClient {

    //@Autowired
   private final ClientConfig clientConfig;

    //create web client and return it
    @Bean
    public  WebClient eSearchClient(){
        return WebClient.builder().baseUrl(clientConfig.baseUrl()).build();
    }

    // confirm the index exist or not, if not create one
    @Bean
    public CommandLineRunner isIndexExist(){
       final WebClient webClient=eSearchClient();
            return cLr ->{
                // create webcall to invok api to searchelastic to check if the index clientConfig.index exist
                webClient.head()
                        .uri("/{index}",clientConfig.index())// localhost:9200/{index} dms_documents
                        .retrieve()
                        .toBodilessEntity()
                        .onErrorResume(WebClientResponseException.NotFound.class,
                                e -> createIndex(clientConfig)
                                        .then(Mono.just(ResponseEntity.ok().build()))
                        )
                        .block();
            };
    }

    private Mono<Void> createIndex(ClientConfig clientConfig) {
     final   WebClient webClient=eSearchClient();
        var body = Map.of(
                "settings", Map.of(
                        "number_of_shards", 1,
                        "number_of_replicas", 0
                ),
                "mappings", Map.of(
                        "properties", Map.of(
                                "id", Map.of("type", "keyword"),
                                "title", Map.of("type", "text",
                                        "fields", Map.of("keyword", Map.of("type", "keyword"))),
                                "tags", Map.of("type", "keyword"),
                                "fileId", Map.of("type", "keyword"),
                                "content", Map.of("type", "text")
                        )
                )
        );
        return webClient.put()
                .uri("/{index}",clientConfig.index())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .then();



    }
}
