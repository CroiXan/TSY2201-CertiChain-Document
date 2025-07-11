package com.certichain.document.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.certichain.document.client.BlockChainGatewayClient;
import com.certichain.document.model.PrivateDocumentAuditLog;

class DocumentAuditControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BlockChainGatewayClient blockChainGatewayClient;

    @InjectMocks
    private DocumentAuditController auditController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(auditController).build();
    }

    @Test
    void queryPrivateAuditLogs_shouldReturnList() throws Exception {
        PrivateDocumentAuditLog log = new PrivateDocumentAuditLog();
        log.setTxID("tx123");
        log.setDocumentId("doc789");
        log.setInstitution("InstA");
        log.setUserId("userX");
        log.setOperation("UPDATE");
        log.setOldState("PENDING");
        log.setNewState("APPROVED");
        log.setTimestamp("2025-07-10T12:34:56Z");

        List<PrivateDocumentAuditLog> logs = Arrays.asList(log);
        when(blockChainGatewayClient.queryPrivateAuditLogs(
                eq("type1"), eq("value1"), eq("2025-07-01"), eq("2025-07-10")))
            .thenReturn(logs);

        mockMvc.perform(get("/api/audit/private")
                    .param("filterType", "type1")
                    .param("filterValue", "value1")
                    .param("startDate", "2025-07-01")
                    .param("endDate", "2025-07-10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].txID").value("tx123"))
               .andExpect(jsonPath("$[0].documentId").value("doc789"))
               .andExpect(jsonPath("$[0].institution").value("InstA"))
               .andExpect(jsonPath("$[0].userId").value("userX"))
               .andExpect(jsonPath("$[0].operation").value("UPDATE"))
               .andExpect(jsonPath("$[0].oldState").value("PENDING"))
               .andExpect(jsonPath("$[0].newState").value("APPROVED"))
               .andExpect(jsonPath("$[0].timestamp").value("2025-07-10T12:34:56Z"));

        verify(blockChainGatewayClient, times(1))
            .queryPrivateAuditLogs("type1", "value1", "2025-07-01", "2025-07-10");
    }
}
