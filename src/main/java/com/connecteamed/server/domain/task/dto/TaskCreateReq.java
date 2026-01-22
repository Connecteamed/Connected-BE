package com.connecteamed.server.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record TaskCreateReq(
        @NotBlank String name,
        @NotBlank String content,
        @NotNull Instant startDate,
        @NotNull Instant dueDate,
        List<Long> assigneeProjectMemberIds
) {
}
