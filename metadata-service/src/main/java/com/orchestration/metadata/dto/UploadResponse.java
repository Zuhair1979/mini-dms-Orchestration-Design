package com.orchestration.metadata.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UploadResponse extends Response{
    String fileId;
    int sizeByte;
    String contentType;
    String sha256;
    String originalFileName;

    public UploadResponse(String fileId, int sizeByte, String contentType, String sha256, String fileName) {
        this.fileId = fileId;
        this.sizeByte = sizeByte;
        this.contentType = contentType;
        this.sha256 = sha256;
        this.originalFileName=fileName;
    }



    public String getResponseMsg() {
        return "UploadResponse{" +
                "fileId='" + fileId + '\'' +
                ", sizeByte=" + sizeByte +
                ", contentType='" + contentType + '\'' +
                ", sha256='" + sha256 + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                '}';
    }
}
