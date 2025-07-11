package com.certichain.document.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.certichain.document.client.BlockChainGatewayClient;
import com.certichain.document.model.DocumentRequest;
import com.certichain.document.model.PrivateDocument;
import com.certichain.document.model.PublicDocument;
import com.certichain.document.model.SearchDocumentRequestInfo;
import com.certichain.document.model.UploadS3FileResponse;

class DocumentServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private DocumentRequestService documentRequestService;

    @Mock
    private BlockChainGatewayClient blockChainGatewayClient;

    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRequest_shouldSetStateCreatedAndDelegate() {
        DocumentRequest input = new DocumentRequest();
        when(documentRequestService.createDocumentRequest(input)).thenReturn(input);

        DocumentRequest result = documentService.createRequest(input);

        assertEquals("CREATED", input.getState());
        assertSame(input, result);
        verify(documentRequestService, times(1)).createDocumentRequest(input);
    }

    @Test
    void createRequestAndUpload_shouldPerformFullFlow() throws IOException {
        DocumentRequest docReq = new DocumentRequest();
        docReq.setRequesterID("userA");
        docReq.setIssuerID("instA");
        DocumentRequest saved = new DocumentRequest();
        saved.setId("id123");
        saved.setRequesterID("userA");
        saved.setIssuerID("instA");
        when(documentRequestService.createDocumentRequest(docReq)).thenReturn(saved);

        UploadS3FileResponse fileResp = new UploadS3FileResponse();
        fileResp.setPath("/bucket/userA-id123");
        fileResp.setHash("hashVal");
        when(s3Service.uploadFile(eq("userA-id123"), any(InputStream.class), eq("application/pdf")))
            .thenReturn(fileResp);

        InputStream dataStream = new ByteArrayInputStream(new byte[]{1,2,3});
        DocumentRequest result = documentService.createRequestAndUpload(docReq, "doc.pdf", dataStream, "application/pdf");

        assertEquals("UPLOADED", docReq.getState());
        assertSame(saved, result);
        verify(s3Service).uploadFile("userA-id123", dataStream, "application/pdf");

        ArgumentCaptor<PublicDocument> pubCaptor = ArgumentCaptor.forClass(PublicDocument.class);
        verify(blockChainGatewayClient).registerDocument(pubCaptor.capture());
        PublicDocument pd = pubCaptor.getValue();
        assertEquals("id123", pd.getDocumentId());
        assertEquals("instA", pd.getInstitution());
        assertEquals("userA", pd.getUserId());

        ArgumentCaptor<PrivateDocument> privCaptor = ArgumentCaptor.forClass(PrivateDocument.class);
        verify(blockChainGatewayClient).savePrivateDocument(privCaptor.capture());
        PrivateDocument pr = privCaptor.getValue();
        assertEquals("id123", pr.getDocumentId());
        assertEquals("/bucket/userA-id123", pr.getPath());
        assertEquals("hashVal", pr.getHash());
        assertEquals("UPLOADED", pr.getState());
    }

    @Test
    void discardRequest_shouldDelegateAndReturn() {
        DocumentRequest dr = new DocumentRequest();
        when(documentRequestService.updateDocumentRequestStatus("x", "DISCARDED"))
            .thenReturn(Optional.of(dr));

        Optional<DocumentRequest> res = documentService.discardRequest("x");
        assertTrue(res.isPresent());
        assertSame(dr, res.get());
        verify(documentRequestService).updateDocumentRequestStatus("x", "DISCARDED");
    }

    @Test
    void discardRequest_notFound_shouldReturnEmpty() {
        when(documentRequestService.updateDocumentRequestStatus("y", "DISCARDED"))
            .thenReturn(Optional.empty());

        Optional<DocumentRequest> res = documentService.discardRequest("y");
        assertFalse(res.isPresent());
    }

    @Test
    void uploadDocument_notFound_shouldReturnEmpty() throws IOException {
        when(documentRequestService.getDocumentRequestById("none"))
            .thenReturn(Optional.empty());

        Optional<DocumentRequest> res = documentService.uploadDocument("none", "f", null, "ct");
        assertFalse(res.isPresent());
        verify(s3Service, never()).uploadFile(any(), any(), any());
    }

    @Test
    void uploadDocument_shouldPerformFullFlow() throws IOException {
        DocumentRequest found = new DocumentRequest();
        found.setId("req1");
        found.setRequesterID("u1");
        found.setIssuerID("i1");
        when(documentRequestService.getDocumentRequestById("req1")).thenReturn(Optional.of(found));

        DocumentRequest updated = new DocumentRequest();
        updated.setId("req1");
        when(documentRequestService.updateDocumentRequest(found)).thenReturn(Optional.of(updated));

        UploadS3FileResponse resp = new UploadS3FileResponse();
        resp.setPath("p");
        resp.setHash("h");
        when(s3Service.uploadFile(eq("u1-req1.pdf"), any(InputStream.class), eq("ct")))
            .thenReturn(resp);

        Optional<DocumentRequest> res = documentService.uploadDocument("req1", "req1.pdf", new ByteArrayInputStream(new byte[]{}), "ct");
        assertTrue(res.isPresent());
        assertSame(updated, res.get());
        verify(s3Service).uploadFile(eq("u1-req1.pdf"), any(InputStream.class), eq("ct"));
        verify(blockChainGatewayClient).registerDocument(any(PublicDocument.class));
        verify(blockChainGatewayClient).savePrivateDocument(any(PrivateDocument.class));
    }

    @Test
    void userSearchRequests_and_institutionSearchRequests_shouldMapResults() {
        Date from = new Date();
        Date to = new Date();
        DocumentRequest dr = new DocumentRequest();
        dr.setId("d1");
        PrivateDocument pd = new PrivateDocument();
        pd.setDocumentId("d1");
        List<PrivateDocument> infos = Collections.singletonList(pd);
        when(documentRequestService.getByFilters("r","i",from,to))
            .thenReturn(Collections.singletonList(dr));
        when(blockChainGatewayClient.privateQueryByUser("r")).thenReturn(infos);
        when(blockChainGatewayClient.privateQueryByInstitution("i")).thenReturn(infos);

        List<SearchDocumentRequestInfo> userRes = documentService.userSearchRequests("r","i",from,to);
        assertEquals(1, userRes.size());
        assertEquals(dr, userRes.get(0).getDocumentRequest());
        assertEquals(pd, userRes.get(0).getPrivateDocument());

        List<SearchDocumentRequestInfo> instRes = documentService.institutionSearchRequests("r","i",from,to);
        assertEquals(1, instRes.size());
        assertEquals(dr, instRes.get(0).getDocumentRequest());
        assertEquals(pd, instRes.get(0).getPrivateDocument());
    }
    
}
