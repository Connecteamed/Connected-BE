package com.connecteamed.server.domain.document.dto;

import java.util.List;

public record DocumentListRes(
        List<Item> documents
) {
    public record Item(
            Long documentId,
            String title,
            String type,
            String uploaderName,
            String uploadDate,
            String downloadUrl,
            boolean canEdit
    ) {}
}
