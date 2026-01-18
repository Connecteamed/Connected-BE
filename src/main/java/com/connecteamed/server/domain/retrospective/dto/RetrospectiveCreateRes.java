package com.connecteamed.server.domain.retrospective.dto;

import java.util.UUID;

public record RetrospectiveCreateRes (
      UUID retrospectiveId,
      String title
) {}