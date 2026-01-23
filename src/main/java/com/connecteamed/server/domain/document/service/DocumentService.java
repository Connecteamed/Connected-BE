package com.connecteamed.server.domain.document.service;

import com.connecteamed.server.domain.document.dto.*;
import com.connecteamed.server.domain.document.enums.DocumentFileType;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    DocumentListRes list(Long projectId);
    DocumentDetailRes detail(Long documentId);
    ResponseEntity<Resource> download(Long documentId);

    DocumentCreateRes createText(Long projectId, String loginId, DocumentCreateTextReq req);
    DocumentUploadRes uploadFile(Long projectId, String loginId, MultipartFile file, DocumentFileType type);
    void updateText(Long documentId, DocumentUpdateTextReq req);
    void delete(Long documentId);
}
