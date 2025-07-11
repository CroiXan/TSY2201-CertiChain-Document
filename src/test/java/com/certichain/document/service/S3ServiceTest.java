package com.certichain.document.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.certichain.document.model.UploadS3FileResponse;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Utilities utilities;

    @InjectMocks
    private S3Service s3Service;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject bucket name
        ReflectionTestUtils.setField(s3Service, "bucket", bucketName);
        // Ensure service uses mocked utilities
        ReflectionTestUtils.setField(s3Service, "utilities", utilities);
    }

    @Test
    void uploadFile_shouldReturnPathAndHash() throws Exception {
        String key = "folder/file.txt";
        byte[] content = "Hello S3".getBytes(StandardCharsets.UTF_8);
        InputStream dataStream = new ByteArrayInputStream(content);
        String eTag = "\"abc123\"";
        URL mockUrl = new URL("https://s3.amazonaws.com/" + bucketName + "/" + key);

        // Stub PutObjectResponse
        PutObjectResponse putResp = PutObjectResponse.builder()
                .eTag(eTag)
                .build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class)))
                .thenReturn(putResp);
        // Stub utilities.getUrl
        when(utilities.getUrl(any(GetUrlRequest.class))).thenReturn(mockUrl);

        UploadS3FileResponse resp = s3Service.uploadFile(key, dataStream, "text/plain");

        assertEquals("abc123", resp.getHash());
        assertEquals(mockUrl.toString(), resp.getPath());
        // Verify putObject request
        verify(s3Client).putObject(
                argThat((PutObjectRequest req) -> req.bucket().equals(bucketName) && req.key().equals(key) && req.contentType().equals("text/plain")),
                any(software.amazon.awssdk.core.sync.RequestBody.class));
        verify(utilities).getUrl(argThat((GetUrlRequest req) -> req.bucket().equals(bucketName) && req.key().equals(key)));
    }

    @Test
    void downloadFile_shouldReturnResourceWithHeaders() throws Exception {
        String key = "download.pdf";
        byte[] bytes = "PDFDATA".getBytes(StandardCharsets.UTF_8);
        GetObjectResponse getResp = GetObjectResponse.builder()
                .contentType("application/pdf")
                .build();
        ResponseInputStream<GetObjectResponse> inputStream = new ResponseInputStream<>(getResp, new ByteArrayInputStream(bytes));

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(inputStream);

        ResponseEntity<Resource> response = s3Service.downloadFile(key);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains(key));
        assertEquals(bytes.length, response.getHeaders().getContentLength());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        Resource resource = response.getBody();
        assertNotNull(resource);
        byte[] result = resource.getInputStream().readAllBytes();
        assertArrayEquals(bytes, result);
        verify(s3Client).getObject(argThat((GetObjectRequest req) -> req.bucket().equals(bucketName) && req.key().equals(key)));
    }
}
