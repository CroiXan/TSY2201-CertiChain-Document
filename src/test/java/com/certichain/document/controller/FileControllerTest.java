package com.certichain.document.controller;

import java.io.InputStream;

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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.certichain.document.service.S3Service;

class FileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    void upload_shouldReturnOkWithFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",            
            "test.txt",         
            MediaType.TEXT_PLAIN_VALUE,
            "Prueba".getBytes()
        );

        when(s3Service.uploadFile(eq("test.txt"), any(InputStream.class), eq(MediaType.TEXT_PLAIN_VALUE)))
            .thenReturn(null);

        mockMvc.perform(multipart("/api/files/upload").file(file))
               .andExpect(status().isOk())
               .andExpect(content().string("Subido: test.txt"));

        verify(s3Service, times(1))
            .uploadFile(eq("test.txt"), any(InputStream.class), eq(MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    void download_shouldReturnResourceWithContent() throws Exception {
        String filename = "test.txt";
        byte[] data = "Content".getBytes();
        ByteArrayResource resource = new ByteArrayResource(data);

        ResponseEntity<Resource> entity = ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(resource);

        when(s3Service.downloadFile(eq(filename))).thenReturn(entity);

        mockMvc.perform(get("/api/files/download/{filename}", filename))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""))
               .andExpect(content().bytes(data));

        verify(s3Service, times(1)).downloadFile(eq(filename));
    }

}
