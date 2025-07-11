package com.certichain.document.controller;

import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.certichain.document.model.DocumentRequest;
import com.certichain.document.model.SearchDocumentRequestInfo;
import com.certichain.document.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createRequest_shouldReturnCreatedDocument() throws Exception {
        DocumentRequest request = new DocumentRequest();
        // populate request fields as needed
        DocumentRequest response = new DocumentRequest();
        // populate response fields
        when(documentService.createRequest(any(DocumentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/document")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(documentService, times(1)).createRequest(any(DocumentRequest.class));
    }

    @Test
    void discardRequest_shouldReturnDiscardedDocument() throws Exception {
        String id = "doc123";
        DocumentRequest response = new DocumentRequest();
        when(documentService.discardRequest(id)).thenReturn(Optional.of(response));

        mockMvc.perform(delete("/api/document").param("Id", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(documentService, times(1)).discardRequest(id);
    }

    @Test
    void uploadDocument_shouldReturnUpdatedDocument() throws Exception {
        String id = "doc456";
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy content".getBytes());
        DocumentRequest response = new DocumentRequest();
        when(documentService.uploadDocument(eq(id), eq("test.pdf"), any(InputStream.class), eq(MediaType.APPLICATION_PDF_VALUE)))
                .thenReturn(Optional.of(response));

        mockMvc.perform(multipart("/api/document/upload/{id}", id)
                .file(file))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(documentService, times(1)).uploadDocument(eq(id), eq("test.pdf"), any(InputStream.class), eq(MediaType.APPLICATION_PDF_VALUE));
    }

    @Test
    void userSearchRequests_shouldReturnList() throws Exception {
        SearchDocumentRequestInfo info = new SearchDocumentRequestInfo();
        when(documentService.userSearchRequests(eq("req1"), eq("iss1"), any(), any()))
                .thenReturn(Collections.singletonList(info));

        mockMvc.perform(get("/api/document/user/search")
                .param("requesterID", "req1")
                .param("issuerID", "iss1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(documentService, times(1)).userSearchRequests(eq("req1"), eq("iss1"), any(), any());
    }

    @Test
    void institutionSearchRequests_shouldReturnList() throws Exception {
        SearchDocumentRequestInfo info = new SearchDocumentRequestInfo();
        when(documentService.institutionSearchRequests(eq("req2"), eq("iss2"), any(), any()))
                .thenReturn(Collections.singletonList(info));

        mockMvc.perform(get("/api/document/institution/search")
                .param("requesterID", "req2")
                .param("issuerID", "iss2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(documentService, times(1)).institutionSearchRequests(eq("req2"), eq("iss2"), any(), any());
    }

}
