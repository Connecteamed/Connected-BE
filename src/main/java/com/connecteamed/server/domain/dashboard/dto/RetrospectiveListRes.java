package com.connecteamed.server.domain.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record RetrospectiveListRes (
        List<RetrospectiveRes> retrospectives
) {
    public record RetrospectiveRes (
            Long id,
            String title,
            String teamName,
            Instant writtenDate
    ) {}
}
