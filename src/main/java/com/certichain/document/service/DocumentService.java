package com.certichain.document.service;

import org.springframework.stereotype.Service;

import com.certichain.document.client.BlockChainGatewayClient;

@Service
public class DocumentService {

    private final S3Service s3Service;
    private final DocumentRequestService documentRequestService;
    private final BlockChainGatewayClient blockChainGatewayClient;
    
    public DocumentService(S3Service s3Service, DocumentRequestService documentRequestService,
            BlockChainGatewayClient blockChainGatewayClient) {
        this.s3Service = s3Service;
        this.documentRequestService = documentRequestService;
        this.blockChainGatewayClient = blockChainGatewayClient;
    }

}
