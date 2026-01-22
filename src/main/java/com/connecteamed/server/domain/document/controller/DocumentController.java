package com.connecteamed.server.domain.document.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.connecteamed.server.domain.document.dto.DocumentCreateRes;
import com.connecteamed.server.domain.document.dto.DocumentCreateTextReq;
import com.connecteamed.server.domain.document.dto.DocumentDetailRes;
import com.connecteamed.server.domain.document.dto.DocumentListRes;
import com.connecteamed.server.domain.document.dto.DocumentUpdateTextReq;
import com.connecteamed.server.domain.document.dto.DocumentUploadRes;
import com.connecteamed.server.domain.document.enums.DocumentFileType;
import com.connecteamed.server.domain.document.service.DocumentService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import com.connecteamed.server.global.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Document", description = "문서 관련 API")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "문서 목록 조회", description = "문서 목록의 조회하는 API입니다.")
    @GetMapping("/projects/{projectId}/documents")
    public ResponseEntity<ApiResponse<DocumentListRes>> list(@PathVariable Long projectId) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(GeneralSuccessCode._OK, documentService.list(projectId))
        );
    }

    @Operation(summary = "문서 상세 조회", description = "문서를 상세를 조회하는 API입니다.")
    @GetMapping("/documents/{documentId}")
    public ResponseEntity<ApiResponse<DocumentDetailRes>> detail(@PathVariable Long documentId) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(GeneralSuccessCode._OK, documentService.detail(documentId))
        );
    }

    @Operation(summary = "문서 다운로드", description = "문서 다운로드 API입니다.")
    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long documentId) {
        return documentService.download(documentId);
    }

    @Operation(summary = "문서 추가(파일 업로드)", description = "문서 추가 (파일 업로드) API입니다.")
    @PostMapping(value = "/projects/{projectId}/documents/upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<DocumentUploadRes>> upload(
            @PathVariable Long projectId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("type") String type
    ) {
        Long projectMemberId = 1L; // TODO 인증에서 꺼내오기
        DocumentFileType fileType = DocumentFileType.valueOf(type);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(GeneralSuccessCode._CREATED, documentService.uploadFile(projectId, projectMemberId, file, fileType))
        );
    }

    @Operation(summary = "문서 추가(텍스트 작성)", description = "문서 추가 (텍스트 작성) API입니다.")
    @PostMapping("/projects/{projectId}/documents/text")
    public ResponseEntity<ApiResponse<DocumentCreateRes>> createText(
            @PathVariable Long projectId,
            @Valid @RequestBody DocumentCreateTextReq req
    ) {
        String loginId = SecurityUtil.getCurrentLoginId();
        return ResponseEntity.ok(
            ApiResponse.onSuccess(GeneralSuccessCode._CREATED, documentService.createText(projectId, loginId, req))
        );
    }

    @Operation(summary = "문서 수정(텍스트)", description = "문서수정(텍스트) API입니다.")
    @PatchMapping("/documents/{documentId}")
    public ResponseEntity<ApiResponse<Void>> updateText(
            @PathVariable Long documentId,
            @RequestBody DocumentUpdateTextReq req
    ) {
        Long projectMemberId = 1L; // TODO 인증에서 꺼내오기
        documentService.updateText(documentId, projectMemberId, req);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(GeneralSuccessCode._OK, null)
        );
    }

    @Operation(summary = "문서 삭제", description = "문서삭제 API입니다.")
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long documentId) {
        Long projectMemberId = 1L; // TODO 인증에서 꺼내오기
        documentService.delete(documentId, projectMemberId);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(GeneralSuccessCode._OK, null)
        );
    }

}
