package com.connecteamed.server.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class S3Uploader {
    private final Optional<S3Client> s3Client;
    private final String bucket;
    private final boolean isEnabled;

    public S3Uploader(
            Optional<S3Client> s3Client,
            @Value("${app.s3.bucket:}") String bucket,
            @Value("${app.s3.access-key:disabled}") String accessKey
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.isEnabled = accessKey != null && !accessKey.isBlank() && !accessKey.equals("disabled") && s3Client.isPresent();
    }

    /**
     * S3에 파일 업로드
     * @param file 업로드할 파일
     * @param dirName 디렉토리 이름 (예: "project", "document")
     * @return S3의 파일 URL
     * @throws IOException 파일 읽기 실패 시
     */
    public String upload(MultipartFile file, String dirName) throws IOException {
        if (!isEnabled) {
            log.warn("S3 is not enabled. Skipping file upload for: {}", file.getOriginalFilename());
            return null;
        }

        try {
            String originalFileName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeFileName = originalFileName.replaceAll("[\\\\/]", "_");
            String key = dirName + "/" + UUID.randomUUID() + "_" + safeFileName;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.get().putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // S3 URL 생성
            return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
        } catch (IOException e) {
            log.error("S3 upload failed for file: {}, dirName: {}", file.getOriginalFilename(), dirName, e);
            throw new IOException("S3 업로드 실패: " + e.getMessage(), e);
        }
    }
}
