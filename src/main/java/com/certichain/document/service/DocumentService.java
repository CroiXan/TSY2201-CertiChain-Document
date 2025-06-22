package com.certichain.document.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import com.certichain.document.client.BlockChainGatewayClient;
import com.certichain.document.model.DocumentRequest;
import com.certichain.document.model.PrivateDocument;
import com.certichain.document.model.PublicDocument;
import com.certichain.document.model.SearchDocumentRequestInfo;
import com.certichain.document.model.UploadS3FileResponse;

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

    public DocumentRequest createRequest(DocumentRequest docRequest) {
        docRequest.setState("CREATED");
        return documentRequestService.createDocumentRequest(docRequest);
    }

    public DocumentRequest createRequestAndUpload(
            DocumentRequest docRequest,
            String fileName,
            InputStream data,
            String contentType) throws IOException {
        docRequest.setState("UPLOADED");

        DocumentRequest newRequest = documentRequestService.createDocumentRequest(docRequest);
        UploadS3FileResponse fileResponse = s3Service.uploadFile(newRequest.getRequesterID() + "-" + newRequest.getId(), data, contentType);

        PublicDocument newDocument = new PublicDocument();
        newDocument.setDocumentId(newRequest.getId());
        newDocument.setInstitution(newRequest.getIssuerID());
        newDocument.setUserId(newRequest.getRequesterID());

        blockChainGatewayClient.registerDocument(newDocument);

        PrivateDocument newDocumentInfo = new PrivateDocument();
        newDocumentInfo.setDocumentId(newRequest.getId());
        newDocumentInfo.setInstitution(newRequest.getIssuerID());
        newDocumentInfo.setUserId(newRequest.getRequesterID());
        newDocumentInfo.setName(fileName);
        newDocumentInfo.setPath(fileResponse.getPath());
        newDocumentInfo.setHash(fileResponse.getHash());
        newDocumentInfo.setState("UPLOADED");

        blockChainGatewayClient.savePrivateDocument(newDocumentInfo);

        return newRequest;
    }

    public Optional<DocumentRequest> discardRequest(String Id) {
        return documentRequestService.updateDocumentRequestStatus(Id, "DISCARDED");
    }

    public Optional<DocumentRequest> uploadDocument(
            String docRequestId,
            String fileName,
            InputStream data,
            String contentType) throws IOException {
        Optional<DocumentRequest> foundRequest = documentRequestService.getDocumentRequestById(docRequestId);

        if (foundRequest.isEmpty()) {
            return foundRequest;
        }
        DocumentRequest request = foundRequest.get();
        request.setState("UPLOADED");

        Optional<DocumentRequest> updatedRequest = documentRequestService.updateDocumentRequest(request);

        UploadS3FileResponse fileResponse = s3Service.uploadFile(request.getRequesterID() + "-" + request.getId() + "." + FilenameUtils.getExtension(fileName), data, contentType);

        PublicDocument newDocument = new PublicDocument();
        newDocument.setDocumentId(request.getId());
        newDocument.setInstitution(request.getIssuerID());
        newDocument.setUserId(request.getRequesterID());

        blockChainGatewayClient.registerDocument(newDocument);

        PrivateDocument newDocumentInfo = new PrivateDocument();
        newDocumentInfo.setDocumentId(request.getId());
        newDocumentInfo.setInstitution(request.getIssuerID());
        newDocumentInfo.setUserId(request.getRequesterID());
        newDocumentInfo.setName(fileName);
        newDocumentInfo.setPath(fileResponse.getPath());
        newDocumentInfo.setHash(fileResponse.getHash());
        newDocumentInfo.setState("UPLOADED");

        blockChainGatewayClient.savePrivateDocument(newDocumentInfo);

        return updatedRequest;
    }

    public List<SearchDocumentRequestInfo> userSearchRequests(
            String requesterID,
            String issuerID,
            Date from,
            Date to){
        return this.searchRequests(requesterID, issuerID, from, to, blockChainGatewayClient.privateQueryByUser(requesterID));
    }

    public List<SearchDocumentRequestInfo> institutionSearchRequests(
            String requesterID,
            String issuerID,
            Date from,
            Date to){
        return this.searchRequests(requesterID, issuerID, from, to, blockChainGatewayClient.privateQueryByInstitution(issuerID));
    }

    private List<SearchDocumentRequestInfo> searchRequests(
            String requesterID,
            String issuerID,
            Date from,
            Date to,
            List<PrivateDocument> documentInfoList) {
        List<DocumentRequest> filterResults = documentRequestService.getByFilters(requesterID, issuerID, from, to); 

        Map<String, PrivateDocument> documentPrivateInfoMap = documentInfoList.stream()
                .collect(Collectors.toMap(PrivateDocument::getDocumentId, Function.identity()));

        List<SearchDocumentRequestInfo> result = filterResults.stream()
                .map(a -> {
                    PrivateDocument b = documentPrivateInfoMap.get(a.getId());
                    return new SearchDocumentRequestInfo(a, b);
                })
                .toList();
        
        return result;
    }

}
