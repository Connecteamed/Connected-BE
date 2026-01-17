package com.connecteamed.server.domain.document.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface S3StorageService {
    String upload(MultipartFile file, String keyPrefix);
    InputStream download(String key);
    String guessDownloadFileName(String key);
}
