package com.connecteamed.server.domain.retrospective.dto;

import java.util.List;

public record RetrospectiveCreateReq (
        String title,
        String projectResult,
        List<Long> taskIds
) {}

