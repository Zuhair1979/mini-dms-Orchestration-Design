package com.orchestration.gatewayserver.fallbackcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
public class FallBackController {

    @RequestMapping("/fallback/meta")
    public ResponseEntity<Map<String,Object>> metaServiceFallBack(){
        return  ResponseEntity.status(503).body(Map.of("Service","MetaData Service","Status","Unavailable","fallback",true));
    }

    @RequestMapping("/fallback/search")
    public ResponseEntity<Map<String, Object>> searchFallBack(){
        return ResponseEntity.status(500).body(Map.of("Service","Elastic Search","Status","Unavailable","fallback",true));
    }


    @RequestMapping("/fallback/storage")
    public ResponseEntity<Map<String, Object>> storageFallBack(){
        return ResponseEntity.status(500).body(Map.of("Service","Storage","Status","Unavailable","fallback",true));
    }
    @RequestMapping("/fallback/default")
    public ResponseEntity<Map<String, Object>> defaultServcieFallBack(){
        return ResponseEntity.status(503).body(Map.of("Status","Unavailable","fallback",true));
    }
}
