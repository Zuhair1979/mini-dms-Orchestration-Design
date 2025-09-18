package com.orchestration.search.escontrollers;

import com.orchestration.search.dto.CreateEsDot;
import com.orchestration.search.elasticservice.EsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class EsController {
   private final EsService service;

    @PutMapping("/search/push")
    public Mono<ResponseEntity<Void>> createSearchDocument(@RequestBody CreateEsDot body,@RequestHeader(value = "X-Correlation-Id", required = false) String cid){
        return  this.service.createDoc(body,cid);
    }
}
