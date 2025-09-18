package com.orchestration.search.elasticservice;

import com.orchestration.search.config.ClientConfig;
import com.orchestration.search.config.ElasticClient;
import com.orchestration.search.dto.CreateEsDot;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EsService {


   private final ElasticClient esClient;
    private final   ClientConfig clientConfig;
    private static final Logger log= LoggerFactory.getLogger(EsService.class);
    // build a client to be send to Elastic
    public Mono<ResponseEntity<Void>> createDoc(CreateEsDot createEsDoc,String cid ){

        ElasticClient esclient;
        WebClient client=esClient.eSearchClient();

        ClientConfig config;
log.info("Search Document creatd cid {} ",cid);
        return client.put()
                .uri("/{index}/_doc/{id}",clientConfig.index(),createEsDoc.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createEsDoc)
                .retrieve()
                .toBodilessEntity();

        }


}
