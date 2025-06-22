package com.certichain.document.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.certichain.document.model.UploadS3FileResponse;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class S3Service {

    private final S3Client s3;
    private final S3Utilities utilities;
    
    @Value("${aws.s3.bucket-name}")
    private String bucket;

    public S3Service(S3Client s3Client) {
        this.s3 = s3Client;
        this.utilities = s3.utilities();
    }

    public UploadS3FileResponse uploadFile(String key, InputStream data, String contentType) throws IOException {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        PutObjectResponse resp = s3.putObject(req, RequestBody.fromInputStream(data, data.available()));

        GetUrlRequest urlRequest = GetUrlRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();

        URL url = utilities.getUrl(urlRequest);
        String hash = resp.eTag().replace("\"", "");

        UploadS3FileResponse response = new UploadS3FileResponse();
        response.setHash(hash);
        response.setPath(url.toString());
        
        return response;
    }

    public ResponseEntity<Resource> downloadFile(String key) throws IOException {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        var s3Object = s3.getObject(req);
        byte[] bytes = IOUtils.toByteArray(s3Object);

        var resource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + key + "\"")
                .contentLength(bytes.length)
                .contentType(org.springframework.http.MediaType.parseMediaType(s3Object.response().contentType()))
                .body(resource);
    }

}
