package com.orchestration.storage.storageservice;

import com.orchestration.storage.config.MinioClientConfig;
import com.orchestration.storage.config.StorageCredentials;
import com.orchestration.storage.dto.UploadResponse;
import io.minio.*;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.orchestration.storage.utilities.FileUtilities.getContentType;
import static com.orchestration.storage.utilities.FileUtilities.sha256Generator;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.http.MediaType.*;


@Service
public class StorageService {

    @Autowired
    MinioClientConfig miniConfig;
    @Autowired
    StorageCredentials storageCredentials;

    UploadResponse uploadResponse;

    private final static Logger log= LoggerFactory.getLogger(StorageService.class);


    // upload multipart file to minio
    // return will be UploadResponse
    public UploadResponse uploadFileToMinio(MultipartFile file,String cid) throws Exception {

        File tmp;
        try {
            // create tmp file and keep original extension
            String suffix =FilenameUtils.getExtension(file.getOriginalFilename());

            tmp = File.createTempFile("upload-", suffix);
            // copy bytes from file(Multipart) to tmp
            file.transferTo(tmp);
            // now we can generate fileId
            String fileId = UUID.randomUUID().toString();

            // sizeBytes
            long sizeByte = tmp.length();
            // get content Type
            String contentType = getContentType(tmp, file.getContentType());
            // get the Hashkey for the document
            String sha256 = sha256Generator(tmp);

            // keep original file name
            Map<String,String> meta = Map.of(
                    "original-filename", file.getOriginalFilename(),
                    "sha256", sha256
            );
            MinioClient client=miniConfig.getClient(storageCredentials);
            // write the tmp file byte to minio
                try(InputStream fi=new FileInputStream(tmp)){ // open tmp file for reading
                    client.putObject(
                            PutObjectArgs.builder()
                                    .bucket(storageCredentials.bucket())
                                    .contentType(contentType)
                                    .object(fileId)
                                    .userMetadata(meta)
                                    .stream(fi,sizeByte,-1)
                                    .build()
                    );

                }
                finally {
                    tmp.delete();
                }
            log.info("Storage Service, create File cid={}",cid);
            return uploadResponse=new UploadResponse(fileId,sizeByte,contentType,sha256,meta.get("original-filename"));
        } catch (Exception e) {
            throw new RuntimeException(e);




        }
    }

    // download the file from minio
    public ResponseEntity<StreamingResponseBody> getFileFromMinio(String id)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient client=miniConfig.getClient(storageCredentials);
        // open an inputstream from minio
        GetObjectResponse inptStream=client.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(storageCredentials.bucket())
                        .object(id)
                        .build()
        );
        // prepare the http response
        // first httpheader, as we inptStream is lazy that is mean it doesn not return the body until we asked
        // but the header is available
        HttpHeaders header=new HttpHeaders();
        // get contentType from inptStream and set it to header
        String contenType=inptStream.headers().get("Content-Type");
        long contentLength=Long.parseLong( inptStream.headers().get("content-Length"));

        header.setContentType(MediaType.parseMediaType(contenType));
        header.setContentLength(contentLength);
        // this part is only to let the client downlaod
        header.setContentDisposition(
                org.springframework.http.ContentDisposition.attachment().filename(id).build()
        );

        // create the real body by coping the inputstream to outputstream
        StreamingResponseBody body=outputStream -> {inptStream.transferTo(outputStream);};

        return new ResponseEntity<>(body,header, HttpStatus.OK);

    }

    // id here if file id
    public UploadResponse findFileById(String id, String cid)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
        // create client to Minio
        MinioClient client=miniConfig.getClient(storageCredentials);

   StatObjectResponse response= client.statObject(
          StatObjectArgs
                  .builder()
                  .bucket(storageCredentials.bucket())
                  .object(id)
                  .build()
        );
    if (response !=null &&  response.object().endsWith(id)) {
        log.info("Document checked in Minio cid {},",cid);
        return new UploadResponse(response.object().toString()
                , response.object().toString().length(),
                response.headers().get("Content-Type"), response.userMetadata().get("sha256"), response.userMetadata().get("original-filename"));

    }
        return null;
    }


}
