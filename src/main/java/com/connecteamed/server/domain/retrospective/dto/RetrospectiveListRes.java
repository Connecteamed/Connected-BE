package com.connecteamed.server.domain.retrospective.dto;

import java.time.Instant;
import java.util.List;

public record RetrospectiveListRes (
        List<RetrospectiveSummary> retrospectives
){
    public record RetrospectiveSummary (
            Long retrospectiveId,
            String title,
            Instant createdAt
    ) {}
}