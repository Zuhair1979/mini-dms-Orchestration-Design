package com.orchestration.metadata.client;

import com.orchestration.metadata.dto.CreateEsDot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

@FeignClient("search")
public interface IElasticSearchFeignClient {
    @PutMapping("/search/push")
    public Mono<ResponseEntity<Void>> createSearchDocument(@RequestBody CreateEsDot body,@RequestHeader(value = "X-Correlation-Id", required = false) String cid);
}
