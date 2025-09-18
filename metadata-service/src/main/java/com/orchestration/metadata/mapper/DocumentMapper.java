package com.orchestration.metadata.mapper;

import com.orchestration.metadata.dto.ResponseDocumentDto;
import com.orchestration.metadata.entity.Document;

public  class DocumentMapper {

    public static ResponseDocumentDto documentToDocumentDto(Document doc){
        return new ResponseDocumentDto(doc.getId(),doc.getTags(),doc.getTitle(),doc.getFileId(),doc.getOriginalFileName(),doc.getSha256());
    }
}
