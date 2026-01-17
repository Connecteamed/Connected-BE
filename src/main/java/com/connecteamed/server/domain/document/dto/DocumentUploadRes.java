package com.connecteamed.server.domain.document.dto;

public record DocumentUploadRes(
        Long documentId,
        String fileName,
        String createdAt
) {}
