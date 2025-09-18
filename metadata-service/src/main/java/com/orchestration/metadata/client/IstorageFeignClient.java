package com.orchestration.metadata.client;

import com.orchestration.metadata.dto.UploadResponse;
import io.minio.errors.InsufficientDataException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@FeignClient("storage")
public interface IstorageFeignClient {
    @GetMapping("/storage/query/{id}") // id is file Id
    public UploadResponse confirmDocumentStorage(@PathVariable String id, @RequestHeader(value = "X-Correlation-Id", required = false) String cid)
            throws ServerException, InsufficientDataException,  IOException, NoSuchAlgorithmException, InvalidKeyException  ;
}
