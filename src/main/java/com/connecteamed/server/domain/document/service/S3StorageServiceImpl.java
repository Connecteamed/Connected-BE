package com.connecteamed.server.domain.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class S3StorageServiceImpl implements S3StorageService {

    private final S3Client s3Client;
    private final String bucket;
    private final String prefix;

    public S3StorageServiceImpl(
            S3Client s3Client,
            @Value("${app.s3.bucket}") String bucket,
            @Value("${app.s3.prefix:documents}") String prefix
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.prefix = prefix;
    }

    @Override
    public String upload(MultipartFile file, String keyPrefix) {
        try {
            String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeName = original.replaceAll("[\\\\/]", "_");
            String key = "%s/%s/%s_%s".formatted(prefix, keyPrefix, UUID.randomUUID(), safeName);

            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key; // DB에는 key 저장 권장
        } catch (Exception e) {
            log.error("S3 upload failed. bucket={}, prefix={}, keyPrefix={}, filename={}, size={}",
                bucket, prefix, keyPrefix, file.getOriginalFilename(), file.getSize(), e);
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    @Override
    public InputStream download(String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(req);
    }

    @Override
    public String guessDownloadFileName(String key) {
        return Paths.get(key).getFileName().toString();
    }
}
