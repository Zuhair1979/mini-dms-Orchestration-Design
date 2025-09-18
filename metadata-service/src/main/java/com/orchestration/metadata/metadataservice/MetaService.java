package com.orchestration.metadata.metadataservice;

import com.netflix.discovery.converters.Auto;
import com.orchestration.metadata.client.IElasticSearchFeignClient;
import com.orchestration.metadata.client.IstorageFeignClient;
import com.orchestration.metadata.controllers.documentcontroller.DocumentController;
import com.orchestration.metadata.dao.DocumentRepository;
import com.orchestration.metadata.dto.CreateDocumentDto;
import com.orchestration.metadata.dto.CreateEsDot;
import com.orchestration.metadata.dto.ResponseDocumentDto;
import com.orchestration.metadata.dto.UploadResponse;
import com.orchestration.metadata.entity.Document;
import com.orchestration.metadata.mapper.DocumentMapper;
import feign.FeignException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;
import org.apache.logging.log4j.util.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;

@Service
public class MetaService implements IstorageFeignClient, IElasticSearchFeignClient {
    @Autowired
    private IstorageFeignClient istorageFeignClient;

    @Autowired
    private IElasticSearchFeignClient iElasticSearchFeignClient;
    private static final Logger log= LoggerFactory.getLogger(DocumentController.class);


    @Autowired
    DocumentRepository documentRepository;

    // insert a record in Postgres/ documents table
    // first check if file is exist then get meta data retrieved from storage call object
    // add tags, title and save it using documentRepository
    public ResponseDocumentDto addNewDocumentInfo(CreateDocumentDto doc, String cid){
        Document newDocument=new Document();
        newDocument.setTags(doc.tags());
        newDocument.setTitle(doc.title());
        newDocument.setFileId(doc.fileId());
        // confirm that the document is exist in minio based on fileId, it calls confirmFile that calls
        UploadResponse response=confirmDocumentStorage(doc.fileId(), cid);
        if(response !=null )
        {
            log.info("meta called Storage and confirm Document doc, {} existance cid {},",response.getFileId(), cid);
            newDocument.setCotentType(response.getContentType());
            newDocument.setSizeByte(response.getSizeByte());
            newDocument.setSha256(response.getSha256());
            newDocument.setOriginalFileName(response.getOriginalFileName());

        }
        else
            throw new RuntimeException("File not Found");
        Document savedDoc=documentRepository.save(newDocument);
        // we need to create CreateEsDot and call createSearchDocument
        CreateEsDot es=new CreateEsDot(savedDoc.getId(),savedDoc.getTitle(),savedDoc.getTags(),savedDoc.getFileId(),"");
        createSearchDocument(es,cid);

       return DocumentMapper.documentToDocumentDto( savedDoc);
    }

    // find document by UUId and return ResponseDocumentDto
    @Transactional(readOnly = true)
    public ResponseDocumentDto getDocumentById(UUID uuid){
        Document doc=new Document();
        doc= this.documentRepository.findById(uuid).
        orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Document Not Found"));
        return DocumentMapper.documentToDocumentDto(doc);

    }

    @Override
    public UploadResponse confirmDocumentStorage(String id,String cid) {

        try {
            return istorageFeignClient.confirmDocumentStorage(id,cid);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file Id not found in storage");

        }
    }

    @Override
    public Mono<ResponseEntity<Void>> createSearchDocument(CreateEsDot body, String cid) {
      return iElasticSearchFeignClient.createSearchDocument(body,cid);
    }
}