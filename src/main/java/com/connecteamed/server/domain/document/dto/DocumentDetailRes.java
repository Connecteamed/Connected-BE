package com.connecteamed.server.domain.document.dto;

public record DocumentDetailRes(
        Long documentId,
        String title,
        String type,
        String content,      // TEXT면 채움
        String downloadUrl,  // 파일이면 채움
        String createdAt,
        String updatedAt
) {}
