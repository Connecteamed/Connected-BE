package com.connecteamed.server.domain.retrospective.dto;

import java.time.Instant;
import java.util.UUID;

public record RetrospectiveDetailRes (
        UUID retrospectiveId,
        String title,
        String projectResult,
        Instant createdAt,
        Long writerId
){}
