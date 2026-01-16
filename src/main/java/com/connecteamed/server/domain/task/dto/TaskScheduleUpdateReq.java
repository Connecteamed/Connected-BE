package com.connecteamed.server.domain.task.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record TaskScheduleUpdateReq(
        @NotNull Instant startDate,
        @NotNull Instant dueDate
) {
}
