package com.orchestration.storage.controllers;

import com.orchestration.storage.dto.UploadResponse;
import com.orchestration.storage.storageservice.StorageService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.compress.archivers.ar.ArArchiveEntry.HEADER;

@RestController
public class StreamingController {

    private static final String CID="X-Correlation-Id";
    private static final Logger log= LoggerFactory.getLogger(StreamingController.class);

    @Autowired
    StorageService  storageService;

    @PostMapping("/storage/input/uploadfile")
    public UploadResponse uploadFile(@RequestPart("file") MultipartFile mF,
                                     HttpServletRequest  cid) throws Exception {
       if (cid == null  )
           log.info("no cid")    ;
        log.info("Storage Controller, create File cid={}",cid.getHeader(CID));
        return storageService.uploadFileToMinio(mF,cid.getHeader(CID));

    }
    // download document from minio based on file id
    @GetMapping("/storage/output/downloader/{id}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable String id) throws Exception{
        return storageService.getFileFromMinio(id);

    }

    // write new method to confirm fileId is exist or not
    @GetMapping("/storage/query/{id}")
    public UploadResponse confirmDocumentStorage(@PathVariable String id, @RequestHeader(value = "X-Correlation-Id", required = false) String cid)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
log.info("Storage check File existence cid {}",cid);
        return storageService.findFileById(id,cid);
    }

    @GetMapping("/storage/debug/headers")
    public Map<String, String> headers(HttpServletRequest req) {
        Map<String, String> m = new LinkedHashMap<>();
        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String n = names.nextElement();
            m.put(n, req.getHeader(n));
        }
        return m;
    }
}
