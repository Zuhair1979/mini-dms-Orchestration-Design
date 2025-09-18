package com.orchestration.storage.dto;

import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UploadResponse extends Response{
    String fileId;
    long sizeByte;
    String contentType;
    String sha256;
    String originalFileName;

    public UploadResponse(String fileId, long sizeByte, String contentType, String sha256, String fileName) {
        this.fileId = fileId;
        this.sizeByte = sizeByte;
        this.contentType = contentType;
        this.sha256 = sha256;
        this.originalFileName=fileName;
    }


    @Override
    public String toString() {
        return "UploadResponse{" +
                "fileId='" + fileId + '\'' +
                ", sizeByte=" + sizeByte +
                ", contentType='" + contentType + '\'' +
                ", sha256='" + sha256 + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                '}';
    }
}
