package com.certichain.document.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.certichain.document.client.BlockChainGatewayClient;
import com.certichain.document.model.PrivateDocumentAuditLog;
import com.certichain.document.model.PublicDocumentAuditLog;

@RestController
@RequestMapping("/api/audit")
public class DocumentAuditController {

    private final BlockChainGatewayClient blockChainGatewayClient;

    public DocumentAuditController(BlockChainGatewayClient blockChainGatewayClient) {
        this.blockChainGatewayClient = blockChainGatewayClient;
    }

    @GetMapping("/private")
    public List<PrivateDocumentAuditLog> queryPrivateAuditLogs(
            @RequestParam String filterType,
            @RequestParam String filterValue,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return blockChainGatewayClient.queryPrivateAuditLogs(filterType, filterValue, startDate, endDate);
    }

    @GetMapping("/public")
    public List<PublicDocumentAuditLog> queryAuditLogs(
            @RequestParam String filterType,
            @RequestParam String filterValue,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return blockChainGatewayClient.queryAuditLogs(filterType, filterValue, startDate, endDate);
    }
}
