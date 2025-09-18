package com.orchestration.gatewayserver.tracking;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TrackingCorr implements GlobalFilter, Ordered {

    private static final String CID="X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // get corrolation id from the exchange/ request part
        String cid=exchange.getRequest().getHeaders().getFirst(CID);

        if(cid == null || cid.isBlank())
            // create Corrolation Id
            cid=java.util.UUID.randomUUID().toString();

        //assign cid to request header for downstream services
        var mutatedRequest=new CorrelationRequestDecorator(exchange.getRequest(),cid);

        // put cid to response that goes to client also
        exchange.getResponse().getHeaders().set(CID,cid);

        // pass the mutated request that has new data in header downstream
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
