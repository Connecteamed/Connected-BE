package com.connecteamed.server.domain.document.service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.connecteamed.server.domain.document.dto.DocumentCreateRes;
import com.connecteamed.server.domain.document.dto.DocumentCreateTextReq;
import com.connecteamed.server.domain.document.dto.DocumentDetailRes;
import com.connecteamed.server.domain.document.dto.DocumentListRes;
import com.connecteamed.server.domain.document.dto.DocumentUpdateTextReq;
import com.connecteamed.server.domain.document.dto.DocumentUploadRes;
import com.connecteamed.server.domain.document.entity.Document;
import com.connecteamed.server.domain.document.enums.DocumentFileType;
import com.connecteamed.server.domain.document.repository.DocumentRepository;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final S3StorageService s3StorageService;

    @Override
    @Transactional(readOnly = true)
    public DocumentListRes list(Long projectId) {
        List<Document> docs =
                documentRepository.findAllByProjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(projectId);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return new DocumentListRes(
                docs.stream()
                        .map(d -> new DocumentListRes.Item(
                                d.getId(),
                                d.getTitle(),
                                d.getFileType().name(),
                                "TODO_업로더명", // 필요하면 projectMember에서 이름 꺼내서 세팅
                                d.getCreatedAt().toLocalDate().format(df),
                                (d.getFileType() != DocumentFileType.TEXT)
                                        ? "/api/documents/" + d.getId() + "/download"
                                        : null,
                                d.getFileType() == DocumentFileType.TEXT
                        ))
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDetailRes detail(Long documentId) {
        Document d = documentRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND, "문서를 찾을 수 없습니다."));

        return new DocumentDetailRes(
                d.getId(),
                d.getTitle(),
                d.getFileType().name(),
                (d.getFileType() == DocumentFileType.TEXT) ? d.getContent() : null,
                (d.getFileType() != DocumentFileType.TEXT) ? "/api/documents/" + d.getId() + "/download" : null,
                d.getCreatedAt().toString(),
                d.getUpdatedAt().toString()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> download(Long documentId) {
        Document d = documentRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND, "문서를 찾을 수 없습니다."));

        if (d.getFileType() == DocumentFileType.TEXT) {
            throw new GeneralException(GeneralErrorCode.FORBIDDEN, "TEXT 문서는 다운로드 대상이 아닙니다.");
        }
        if (d.getFileUrl() == null || d.getFileUrl().isBlank()) {
            throw new GeneralException(GeneralErrorCode.NOT_FOUND, "파일 key가 없습니다.");
        }

        String key = d.getFileUrl();

        try {
            InputStream in = s3StorageService.download(key);
            Resource resource = new InputStreamResource(in);

            // 다운로드 파일명: DB title 우선, 없으면 key에서 파일명 뽑기
            String filename = (d.getTitle() != null && !d.getTitle().isBlank())
                    ? d.getTitle()
                    : key.substring(key.lastIndexOf('/') + 1);

            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded)
                    .body(resource);

        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.NOT_FOUND, "파일 다운로드에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public DocumentCreateRes createText(Long projectId, Long projectMemberId, DocumentCreateTextReq req) {
        Project projectRef = projectRepository.getReferenceById(projectId);
        ProjectMember projectMemberRef = projectMemberRepository.getReferenceById(projectMemberId);

        Document d = Document.createText(projectRef, projectMemberRef, req.title(), req.content());
        documentRepository.save(d);

        return new DocumentCreateRes(d.getId(), d.getCreatedAt().toString());
    }

    @Override
    @Transactional
    public DocumentUploadRes uploadFile(Long projectId, Long projectMemberId, MultipartFile file, DocumentFileType type) {
        if (type == DocumentFileType.TEXT) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST, "TEXT는 파일 업로드 타입이 아닙니다.");
        }

        Project projectRef = projectRepository.getReferenceById(projectId);
        ProjectMember projectMemberRef = projectMemberRepository.getReferenceById(projectMemberId);

        String fileUrl = s3StorageService.upload(file, "project-" + projectId);

        String title = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                ? "file"
                : file.getOriginalFilename();

        Document d = Document.createFile(projectRef, projectMemberRef, title, type, fileUrl);
        documentRepository.save(d);

        return new DocumentUploadRes(d.getId(), title, d.getCreatedAt().toString());
    }

    @Override
    @Transactional
    public void updateText(Long documentId, Long projectMemberId, DocumentUpdateTextReq req) {
        Document d = documentRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND, "문서를 찾을 수 없습니다."));

        if (d.getFileType() != DocumentFileType.TEXT) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST, "텍스트 문서만 수정할 수 있습니다.");
        }

        d.updateText(req.title(), req.content());
    }

    @Override
    @Transactional
    public void delete(Long documentId, Long projectMemberId) {
        Document d = documentRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND, "문서를 찾을 수 없습니다."));

        // 파일 문서면 필요 시 S3 삭제 정책 추가 가능
        // if (d.getFileType() != DocumentFileType.TEXT) { ... }

        d.softDelete();
    }
}
