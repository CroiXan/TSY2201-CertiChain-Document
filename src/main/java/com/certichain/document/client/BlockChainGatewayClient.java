package com.certichain.document.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.certichain.document.model.PrivateDocument;
import com.certichain.document.model.PrivateDocumentAuditLog;
import com.certichain.document.model.PublicDocument;
import com.certichain.document.model.PublicDocumentAuditLog;

@FeignClient(name = "gateway-service", url = "http://localhost:8080/")
public interface BlockChainGatewayClient {

    @PostMapping("/privatedocuments")
    public void savePrivateDocument(@RequestBody PrivateDocument doc);

    @PutMapping("/privatedocuments/{id}/state")
    public void updateState(@PathVariable String id, @RequestParam String newState);

    @GetMapping("/privatedocuments/{id}")
    public PrivateDocument getPrivateDocument(@PathVariable String id);

    @GetMapping("/privatedocuments/institution/{institution}")
    public List<PrivateDocument> privateQueryByInstitution(@PathVariable String institution);

    @GetMapping("/privatedocuments/user/{userId}")
    public List<PrivateDocument> privateQueryByUser(@PathVariable String userId);

    @GetMapping("/privatedocuments/audit")
    public List<PrivateDocumentAuditLog> queryPrivateAuditLogs(
            @RequestParam String filterType,
            @RequestParam String filterValue,
            @RequestParam String startDate,
            @RequestParam String endDate
    );

    @PostMapping("/publicdocuments")
    public void registerDocument(@RequestBody PublicDocument document);

    @GetMapping("/publicdocuments/{id}")
    public PublicDocument getDocumentById(@PathVariable String id);

    @GetMapping("/publicdocuments/institution/{institution}")
    public List<PublicDocument> publicQueryByInstitution(@PathVariable String institution);

    @GetMapping("/publicdocuments/user/{userId}")
    public List<PublicDocument> publicQueryByUser(@PathVariable String userId);

    @GetMapping("/publicdocuments/audit")
    public List<PublicDocumentAuditLog> queryAuditLogs(
        @RequestParam String filterType,
        @RequestParam String filterValue,
        @RequestParam String startDate,
        @RequestParam String endDate
    );

}
