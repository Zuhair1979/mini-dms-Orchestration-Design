package com.orchestration.metadata.controllers.storagefeigcontroller;

import com.orchestration.metadata.dto.UploadResponse;
import com.orchestration.metadata.metadataservice.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;

@RestController
public class StorageFeignController {
    @Autowired
    MetaService metaService;
    // confirm the file from Meta service

    @GetMapping(value="/meta/confirmfile/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadResponse> confirmFileStorageFeign(@PathVariable String id,@RequestHeader(value = "X-Correlation-Id", required = false) String cid){
        try {

            UploadResponse resposeBody=metaService.confirmDocumentStorage(id,cid);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Object-Content-Type", resposeBody.getContentType())
                    .header("sha256",resposeBody.getSha256())
                    .body(resposeBody);



        }
        catch (Exception e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND," File not Found");
        }

    }

}
