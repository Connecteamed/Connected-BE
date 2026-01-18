package com.connecteamed.server.domain.retrospective.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RetrospectiveListRes (
        List<RetrospectiveSummary> retrospectives
){
    public record RetrospectiveSummary (
            UUID retrospectiveId,
            String title,
            Instant createdAt
    ) {}
}