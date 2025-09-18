package com.orchestration.metadata.controllers.documentcontroller;

import com.orchestration.metadata.dto.CreateDocumentDto;
import com.orchestration.metadata.dto.CurdResponse;
import com.orchestration.metadata.dto.ResponseDocumentDto;
import com.orchestration.metadata.metadataservice.MetaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DocumentController {
 //   @Autowired
    private final MetaService metaService;
   private static final String CID="X-Correlation-Id";
   private static final Logger log= LoggerFactory.getLogger(DocumentController.class);


   //create Document end point
    @PostMapping("/meta/create")
    public ResponseEntity<ResponseDocumentDto> createDocument(@Valid @RequestBody CreateDocumentDto doc, @RequestHeader(value = "X-Correlation-Id", required = false) String cid) {
        ResponseDocumentDto resBody = this.metaService.addNewDocumentInfo(doc,cid);
        return ResponseEntity.status(HttpStatus.CREATED).body(resBody);
    }
    // get ResponseDocumentDto by id
    @GetMapping("/meta/get/{id}")
    public ResponseEntity<ResponseDocumentDto> getDocument(@Valid @PathVariable UUID id){
    ResponseDocumentDto resBody=this.metaService.getDocumentById(id);
    if (resBody != null)
    return ResponseEntity.status(HttpStatus.OK).body(resBody);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    @GetMapping("/meta/slow")
    public ResponseEntity<String> slow(@RequestParam(required=false) Boolean fail,@RequestHeader(value ="X-Correlation-Id", required = false) String cid) throws InterruptedException {
        log.info("cid : {} ",cid);
        if (Boolean.TRUE.equals(fail)) return ResponseEntity.status(500).body("boom");
        var x=Math.random();
        if (x < 0.3)
            return ResponseEntity.status(500).body("boom "+x);

        Thread.sleep(1500);
        return ResponseEntity.ok("slow-ok");
    }


}
