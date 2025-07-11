package com.certichain.document.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.certichain.document.model.DocumentRequest;
import com.certichain.document.repository.DocumentRequestRepository;

class DocumentRequestServiceTest {

    @Mock
    private DocumentRequestRepository repository;

    @Mock
    private MongoTemplate template;

    @InjectMocks
    private DocumentRequestService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_shouldReturnAllRequests() {
        DocumentRequest dr = new DocumentRequest();
        when(repository.findAll()).thenReturn(Collections.singletonList(dr));

        List<DocumentRequest> result = service.getAll();

        assertEquals(1, result.size());
        assertSame(dr, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void createDocumentRequest_shouldInitializeDateAndSave() {
        DocumentRequest req = new DocumentRequest();
        when(repository.save(any(DocumentRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        DocumentRequest result = service.createDocumentRequest(req);

        assertNotNull(result.getDate(), "Date should be initialized");
        verify(repository).save(req);
    }

    @Test
    void updateDocumentRequest_existing_shouldSaveAndReturnOptional() {
        DocumentRequest req = new DocumentRequest();
        req.setId("1");
        when(repository.findById("1")).thenReturn(Optional.of(req));
        when(repository.save(req)).thenReturn(req);

        Optional<DocumentRequest> result = service.updateDocumentRequest(req);

        assertTrue(result.isPresent());
        assertSame(req, result.get());
        verify(repository).findById("1");
        verify(repository).save(req);
    }

    @Test
    void updateDocumentRequest_nonExisting_shouldReturnEmpty() {
        DocumentRequest req = new DocumentRequest();
        req.setId("x");
        when(repository.findById("x")).thenReturn(Optional.empty());

        Optional<DocumentRequest> result = service.updateDocumentRequest(req);

        assertFalse(result.isPresent());
        verify(repository).findById("x");
        verify(repository, never()).save(any());
    }

    @Test
    void deleteDocumentRequestById_existing_shouldDeleteAndReturnTrue() {
        DocumentRequest dr = new DocumentRequest();
        when(repository.findById("1")).thenReturn(Optional.of(dr));
        doNothing().when(repository).deleteById("1");

        boolean deleted = service.deleteDocumentRequestById("1");

        assertTrue(deleted);
        verify(repository, times(2)).deleteById("1");
        verify(repository).findById("1");
    }

    @Test
    void deleteDocumentRequestById_nonExisting_shouldReturnFalse() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        boolean deleted = service.deleteDocumentRequestById("x");

        assertFalse(deleted);
        verify(repository).deleteById("x");
        verify(repository).findById("x");
        verify(repository, times(1)).deleteById("x");
    }

    @Test
    void getByRequester_shouldReturnMatchingRequests() {
        DocumentRequest dr = new DocumentRequest();
        when(repository.findByRequesterID("r1")).thenReturn(Collections.singletonList(dr));

        List<DocumentRequest> result = service.getByRequester("r1");

        assertEquals(1, result.size());
        assertSame(dr, result.get(0));
        verify(repository).findByRequesterID("r1");
    }

    @Test
    void getByIssuer_shouldReturnMatchingRequests() {
        DocumentRequest dr = new DocumentRequest();
        when(repository.findByIssuerID("i1")).thenReturn(Collections.singletonList(dr));

        List<DocumentRequest> result = service.getByIssuer("i1");

        assertEquals(1, result.size());
        assertSame(dr, result.get(0));
        verify(repository).findByIssuerID("i1");
    }

    @Test
    void getByDate_shouldReturnRequestsInRange() {
        Date start = new Date();
        Date end = new Date();
        DocumentRequest dr = new DocumentRequest();
        when(repository.findByDateBetween(start, end)).thenReturn(Collections.singletonList(dr));

        List<DocumentRequest> result = service.getByDate(start, end);

        assertEquals(1, result.size());
        assertSame(dr, result.get(0));
        verify(repository).findByDateBetween(start, end);
    }

}
