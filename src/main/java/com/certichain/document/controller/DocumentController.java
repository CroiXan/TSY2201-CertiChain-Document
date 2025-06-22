package com.certichain.document.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.certichain.document.model.DocumentRequest;
import com.certichain.document.model.SearchDocumentRequestInfo;
import com.certichain.document.service.DocumentService;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<DocumentRequest> createRequest(@RequestBody DocumentRequest docRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(documentService.createRequest(docRequest));
    }

    @DeleteMapping
    public ResponseEntity<DocumentRequest> discardRequest(String Id) {
        return ResponseEntity.status(HttpStatus.OK).body(documentService.discardRequest(Id).orElse(new DocumentRequest()));
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<DocumentRequest> uploadDocument(
            @PathVariable String docRequestId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(
            documentService.uploadDocument(
                docRequestId, 
                file.getOriginalFilename(), 
                file.getInputStream(), 
                file.getContentType()).orElse(new DocumentRequest()));
        
    }

    @GetMapping("/user/search")
    public ResponseEntity<List<SearchDocumentRequestInfo>> userSearchRequests(
            @RequestParam(required = false) String requesterID,
            @RequestParam(required = false) String issuerID,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate){
        return ResponseEntity.status(HttpStatus.OK).body(documentService.userSearchRequests(requesterID, issuerID, endDate, endDate));
    }

    @GetMapping("/institution/search")
    public ResponseEntity<List<SearchDocumentRequestInfo>> institutionSearchRequests(
            @RequestParam(required = false) String requesterID,
            @RequestParam(required = false) String issuerID,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate){
        return ResponseEntity.status(HttpStatus.OK).body(documentService.institutionSearchRequests(requesterID, issuerID, endDate, endDate));
    }

}
