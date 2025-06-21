package com.certichain.document.controller;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.certichain.document.model.DocumentRequest;
import com.certichain.document.service.DocumentRequestService;

@RestController
@RequestMapping("/api/requests")
public class DocumentRequestController {

    private final DocumentRequestService service;

    public DocumentRequestController(DocumentRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentRequest> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentRequest create(@RequestBody DocumentRequest docRequest) {
        return service.createDocumentRequest(docRequest);
    }

    @PutMapping
    public ResponseEntity<DocumentRequest> update(@RequestBody DocumentRequest cambios) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateDocumentRequest(cambios).orElse(new DocumentRequest()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteDocumentRequestById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/requester/{requesterID}")
    public List<DocumentRequest> buscarPorRequester(
            @PathVariable String requesterID
    ) {
        return service.getByRequester(requesterID);
    }

    @GetMapping("/issuer/{issuerID}")
    public List<DocumentRequest> buscarPorIssuer(
            @PathVariable String issuerID
    ) {
        return service.getByIssuer(issuerID);
    }

    @GetMapping("/dates")
    public List<DocumentRequest> buscarPorFechaRango(
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end
    ) {
        return service.getByDate(start, end);
    }

    @GetMapping("/search")
    public List<DocumentRequest> search(
        @RequestParam(required = false) String requesterID,
        @RequestParam(required = false) String issuerID,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate
    ) {
        return service.getByFilters(requesterID, issuerID, startDate, endDate);
    }

}
