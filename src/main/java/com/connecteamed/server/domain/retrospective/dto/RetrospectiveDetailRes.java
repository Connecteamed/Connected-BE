package com.connecteamed.server.domain.retrospective.dto;

import java.time.Instant;

public record RetrospectiveDetailRes (
        Long retrospectiveId,
        String title,
        String projectResult,
        Instant createdAt,
        Long writerId
){}
