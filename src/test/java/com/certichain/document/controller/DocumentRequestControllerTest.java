package com.certichain.document.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.certichain.document.model.DocumentRequest;
import com.certichain.document.service.DocumentRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;

class DocumentRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentRequestService service;

    @InjectMocks
    private DocumentRequestController controller;

    private ObjectMapper objectMapper;
    private SimpleDateFormat dateFmt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        dateFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        DocumentRequest d1 = new DocumentRequest();
        d1.setId("1"); d1.setRequesterID("r1"); d1.setIssuerID("i1"); d1.setState("NEW");
        DocumentRequest d2 = new DocumentRequest();
        d2.setId("2"); d2.setRequesterID("r2"); d2.setIssuerID("i2"); d2.setState("PENDING");
        when(service.getAll()).thenReturn(Arrays.asList(d1, d2));

        mockMvc.perform(get("/api/requests"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value("1"))
               .andExpect(jsonPath("$[1].state").value("PENDING"));

        verify(service, times(1)).getAll();
    }

    @Test
    void create_shouldReturnCreatedDocument() throws Exception {
        DocumentRequest req = new DocumentRequest();
        req.setRequesterID("r3"); req.setIssuerID("i3");
        DocumentRequest created = new DocumentRequest();
        created.setId("3"); created.setRequesterID("r3"); created.setIssuerID("i3");
        when(service.createDocumentRequest(any(DocumentRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/requests")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value("3"));

        verify(service, times(1)).createDocumentRequest(any(DocumentRequest.class));
    }

    @Test
    void update_shouldReturnUpdatedDocument() throws Exception {
        DocumentRequest changes = new DocumentRequest();
        changes.setId("4"); changes.setState("APPROVED");
        DocumentRequest updated = new DocumentRequest();
        updated.setId("4"); updated.setState("APPROVED");
        when(service.updateDocumentRequest(any(DocumentRequest.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/requests")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(changes)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.state").value("APPROVED"));

        verify(service, times(1)).updateDocumentRequest(any(DocumentRequest.class));
    }

    @Test
    void delete_notFound_shouldReturn404() throws Exception {
        when(service.deleteDocumentRequestById("x")).thenReturn(false);

        mockMvc.perform(delete("/api/requests/{id}", "x"))
               .andExpect(status().isNotFound());

        verify(service, times(1)).deleteDocumentRequestById("x");
    }

    @Test
    void delete_found_shouldReturnNoContent() throws Exception {
        when(service.deleteDocumentRequestById("y")).thenReturn(true);

        mockMvc.perform(delete("/api/requests/{id}", "y"))
               .andExpect(status().isNoContent());

        verify(service, times(1)).deleteDocumentRequestById("y");
    }

    @Test
    void buscarPorRequester_shouldReturnList() throws Exception {
        DocumentRequest d = new DocumentRequest();
        d.setRequesterID("rq");
        when(service.getByRequester("rq")).thenReturn(Collections.singletonList(d));

        mockMvc.perform(get("/api/requests/requester/{requesterID}", "rq"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].requesterID").value("rq"));

        verify(service, times(1)).getByRequester("rq");
    }

    @Test
    void buscarPorIssuer_shouldReturnList() throws Exception {
        DocumentRequest d = new DocumentRequest();
        d.setIssuerID("is");
        when(service.getByIssuer("is")).thenReturn(Collections.singletonList(d));

        mockMvc.perform(get("/api/requests/issuer/{issuerID}", "is"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].issuerID").value("is"));

        verify(service, times(1)).getByIssuer("is");
    }

    @Test
    void buscarPorFechaRango_shouldReturnList() throws Exception {
        DocumentRequest d = new DocumentRequest();
        d.setId("5"); d.setState("NEW");
        when(service.getByDate(any(), any())).thenReturn(Collections.singletonList(d));

        mockMvc.perform(get("/api/requests/dates")
               .param("startDate", "2025-07-01")
               .param("endDate", "2025-07-10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value("5"));

        verify(service, times(1)).getByDate(any(), any());
    }

    @Test
    void search_shouldReturnList() throws Exception {
        DocumentRequest d = new DocumentRequest();
        d.setRequesterID("rq2"); d.setIssuerID("is2");
        when(service.getByFilters(eq("rq2"), eq("is2"), any(), any()))
            .thenReturn(Collections.singletonList(d));

        mockMvc.perform(get("/api/requests/search")
               .param("requesterID", "rq2")
               .param("issuerID", "is2"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].requesterID").value("rq2"));

        verify(service, times(1)).getByFilters(eq("rq2"), eq("is2"), any(), any());
    }

}
