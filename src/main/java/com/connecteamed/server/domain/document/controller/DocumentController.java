package com.connecteamed.server.domain.document.controller;

import com.connecteamed.server.domain.document.dto.*;
import com.connecteamed.server.domain.document.enums.DocumentFileType;
import com.connecteamed.server.domain.document.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // 문서 목록 조회
    @GetMapping("/projects/{projectId}/documents")
    public ApiResponse<DocumentListRes> list(@PathVariable Long projectId) {
        return ApiResponse.success(documentService.list(projectId));
    }

    // 문서 상세 조회(모달)
    @GetMapping("/documents/{documentId}")
    public ApiResponse<DocumentDetailRes> detail(@PathVariable Long documentId) {
        return ApiResponse.success(documentService.detail(documentId));
    }

    // 문서 다운로드(바이너리)
    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long documentId) {
        return documentService.download(documentId);
    }

    // 문서 추가(파일 업로드)
    @PostMapping(value = "/projects/{projectId}/documents/upload", consumes = "multipart/form-data")
    public ApiResponse<DocumentUploadRes> upload(
            @PathVariable Long projectId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("type") String type
    ) {
        Long projectMemberId = 1L; // TODO 인증에서 꺼내오기
        DocumentFileType fileType = DocumentFileType.valueOf(type);
        return ApiResponse.success(documentService.uploadFile(projectId, projectMemberId, file, fileType));
    }

    // 문서 추가(텍스트 작성)
    @PostMapping("/projects/{projectId}/documents/text")
    public ApiResponse<DocumentCreateRes> createText(
            @PathVariable Long projectId,
            @RequestBody DocumentCreateTextReq req
    ) {
        Long projectMemberId = 1L; // TODO 인증에서 꺼내오기
        return ApiResponse.success(documentService.createText(projectId, projectMemberId, req));
    }

    // 문서 수정(텍스트만)
    @PatchMapping("/documents/{documentId}")
    public ApiResponse<Void> updateText(
            @PathVariable Long documentId,
            @RequestBody DocumentUpdateTextReq req
    ) {
        Long projectMemberId = 1L; // TODO 인증에서 꺼내오기
        documentService.updateText(documentId, projectMemberId, req);
        return ApiResponse.success(null);
    }

    // 문서 삭제(소프트 삭제)
    @DeleteMapping("/documents/{documentId}")
    public ApiResponse<Void> delete(@PathVariable Long documentId) {
        Long projectMemberId = 1L; // TODO 인증에서 꺼내오기
        documentService.delete(documentId, projectMemberId);
        return ApiResponse.success(null);
    }

    // 공통 응답 래퍼(이미 프로젝트에 있으면 이거는 빼고 기존거 쓰시면 됩니다)
    public record ApiResponse<T>(String status, T data) {
        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>("success", data);
        }
    }
}
