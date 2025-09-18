package com.orchestration.gatewayserver.tracking;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;

public class CorrelationRequestDecorator extends ServerHttpRequestDecorator {
    private final HttpHeaders newHeader;

    public CorrelationRequestDecorator(ServerHttpRequest delegate, String correlationId) {
        super(delegate);

        // create new Header
        HttpHeaders header= new HttpHeaders();
        header.putAll(delegate.getHeaders());
        header.set("X-Correlation-Id",correlationId);
        this.newHeader=header;
    }


    @Override
    public HttpHeaders getHeaders() {         // <-- correct override
        return this.newHeader;
    }
}
